package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({FilmDbStorage.class, FilmRowMapper.class, GenreRowMapper.class})
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;

    @Test
    void testSaveAndFindById() {
        Film film = createTestFilm();
        Film saved = filmStorage.save(film);

        Optional<Film> found = filmStorage.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getName(), found.get().getName());
    }

    @Test
    void testFindAll() {
        filmStorage.save(createTestFilm());
        filmStorage.save(createTestFilm2());

        Collection<Film> films = filmStorage.findAll();
        assertFalse(films.isEmpty());
        assertTrue(films.size() >= 2);
    }

    @Test
    void testUpdate() {
        Film film = filmStorage.save(createTestFilm());
        film.setName("Обновлённое название");
        filmStorage.update(film);

        Optional<Film> updated = filmStorage.findById(film.getId());
        assertTrue(updated.isPresent());
        assertEquals("Обновлённое название", updated.get().getName());
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setName("Тестовый фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }

    private Film createTestFilm2() {
        Film film = new Film();
        film.setName("Тестовый фильм 2");
        film.setDescription("Описание 2");
        film.setReleaseDate(LocalDate.of(2001, 1, 1));
        film.setDuration(130);
        return film;
    }
}