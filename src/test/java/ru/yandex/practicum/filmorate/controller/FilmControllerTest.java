package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController controller;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();

        JdbcTemplate jdbcTemplate = null;

        FilmService filmService = new FilmService(filmStorage, userStorage, jdbcTemplate);
        controller = new FilmController(filmStorage, filmService);
    }

    @Test
    void shouldCreateValidFilm() {
        Film film = new Film();
        film.setName("Матрица");
        film.setDescription("Фильм про симуляцию реальности");
        film.setReleaseDate(LocalDate.of(1999, 3, 31));
        film.setDuration(136);

        Film created = controller.create(film);

        assertNotNull(created.getId());
        assertEquals("Матрица", created.getName());
    }

    @Test
    void shouldNotCreateFilmWithBlankName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldNotCreateFilmWithDescriptionTooLong() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldNotCreateFilmWithReleaseDateTooEarly() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldCreateFilmExactlyOnFirstFilmDate() {
        Film film = new Film();
        film.setName("Прибытие поезда");
        film.setDescription("Первый фильм");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(1);

        Film created = controller.create(film);
        assertNotNull(created.getId());
    }

    @Test
    void shouldNotCreateFilmWithNegativeDuration() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-10);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void shouldNotCreateFilmWithZeroDuration() {
        Film film = new Film();
        film.setName("Фильм");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(0);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }
}