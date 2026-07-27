package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }
        if (film.getReleaseDate() != null &&
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность должна быть положительным числом");
        }
    }

    public Film create(Film film) {
        validateFilm(film);


        if (film.getMpa() != null) {
            int mpaId = film.getMpa().getId();
            if (mpaId != 0) {
                boolean mpaExists = filmStorage.mpaExists(mpaId);
                if (!mpaExists) {
                    throw new NotFoundException("Рейтинг MPA с id " + mpaId + " не найден");
                }
            }
        }


        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                int genreId = genre.getId();
                if (genreId != 0) {
                    boolean genreExists = filmStorage.genreExists(genreId);
                    if (!genreExists) {
                        throw new NotFoundException("Жанр с id " + genreId + " не найден");
                    }
                }
            }
        }

        return filmStorage.save(film);
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("id должен быть указан");
        }
        if (!filmStorage.existsById(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }


        if (film.getMpa() != null && film.getMpa().getId() != 0) {
            boolean mpaExists = filmStorage.mpaExists(film.getMpa().getId());
            if (!mpaExists) {
                throw new NotFoundException("Рейтинг MPA с id " + film.getMpa().getId() + " не найден");
            }
        }


        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                boolean genreExists = filmStorage.genreExists(genre.getId());
                if (!genreExists) {
                    throw new NotFoundException("Жанр с id " + genre.getId() + " не найден");
                }
            }
        }

        validateFilm(film);
        return filmStorage.update(film);
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        film.getLikes().add(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден"));

        userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        film.getLikes().remove(userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }

        Collection<Film> allFilms = filmStorage.findAll();
        System.out.println("Всего фильмов в базе: " + allFilms.size());

        return filmStorage.findAll().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikesCount(), f1.getLikesCount()))
                .limit(count)
                .collect(Collectors.toList());
    }
}