package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import org.springframework.context.annotation.Primary;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT f.*, m.id as mpa_id, m.name as mpa_name FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.id";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper());

        for (Film film : films) {
            loadGenresForFilm(film);
            loadLikesForFilm(film);
        }
        return films;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT f.*, m.id as mpa_id, m.name as mpa_name FROM films f " +
                "LEFT JOIN mpa_ratings m ON f.mpa_id = m.id WHERE f.id = ?";
        List<Film> films = jdbcTemplate.query(sql, new FilmRowMapper(), id);

        if (films.isEmpty()) {
            return Optional.empty();
        }
        Film film = films.get(0);
        loadGenresForFilm(film);
        loadLikesForFilm(film);
        return Optional.of(film);
    }

    @Override
    public Film save(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            Integer mpaId = null;
            if (film.getMpa() != null && film.getMpa().getId() != 0) {
                mpaId = film.getMpa().getId();
            }
            ps.setObject(5, mpaId);
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        System.out.println("СОХРАНЁН ID: " + film.getId());
        saveGenresForFilm(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId()
        );


        String deleteGenresSql = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteGenresSql, film.getId());
        saveGenresForFilm(film);


        String deleteLikesSql = "DELETE FROM film_likes WHERE film_id = ?";
        jdbcTemplate.update(deleteLikesSql, film.getId());
        saveLikesForFilm(film);

        return film;
    }


    private void saveLikesForFilm(Film film) {
        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
            for (Long userId : film.getLikes()) {
                jdbcTemplate.update(sql, film.getId(), userId);
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE from films WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM films WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private void loadGenresForFilm(Film film) {
        String sql = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genre fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new GenreRowMapper(), film.getId());
        film.setGenres(new HashSet<>(genres));
    }

    private void loadLikesForFilm(Film film) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        List<Long> userIds = jdbcTemplate.queryForList(sql, Long.class, film.getId());
        film.setLikes(new HashSet<>(userIds));
    }

    private void saveGenresForFilm(Film film) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                if (genre.getId() != 0) {
                    jdbcTemplate.update(sql, film.getId(), genre.getId());
                }
            }
        }
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getLong("id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));

            int mpaId = rs.getInt("mpa_id");
            if (!rs.wasNull()) {
                film.setMpa(new MpaRating(mpaId, rs.getString("mpa_name")));
            }

            return film;
        }
    }

    private static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Genre(rs.getInt("id"), rs.getString("name"));
        }
    }

    @Override
    public boolean mpaExists(int mpaId) {
        String sql = "SELECT COUNT(*) FROM mpa_ratings WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, mpaId);
        return count != null && count > 0;
    }

    @Override
    public boolean genreExists(int genreId) {
        String sql = "SELECT COUNT(*) FROM genres WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreId);
        return count != null && count > 0;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }
}