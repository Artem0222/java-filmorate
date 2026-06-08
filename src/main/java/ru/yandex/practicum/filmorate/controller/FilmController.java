package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/films")
@RestController
public class FilmController {

    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }


    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new RuntimeException("Фильм не найден"));
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validateFilm(film);
        return filmStorage.save(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film.getId() == null) {
            throw new ValidationException("id должен быть указан");
        }
        if (!filmStorage.existsById(film.getId())) {
            throw new ValidationException("Фильм не найден");
        }
        validateFilm(film);
        return filmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, id);
        filmService.addLike(id, userId);
}
@DeleteMapping("/{id}/like/{userId}")
public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
    log.info("Пользователь {} удаляет лайк у фильма {}", userId, id);
    filmService.removeLike(id, userId);
}

@GetMapping("/popular")
public List<Film> getPopular(@RequestParam(required = false) Integer count) {
    log.info("Запрос популярных фильмов, count={}", count);
   return filmService.getPopularFilms(count);
}

private void validateFilm(Film film) {
    if (film.getName() == null || film.getName().isBlank()) {
        throw new ValidationException("Название фильма не может быть пустым");
    }
    if (film.getDescription() != null && film.getDescription().length() > 200) {
        throw new ValidationException("Максимальная длина описания — 200 символов");
    }
    if (film.getReleaseDate() == null) {
        throw new ValidationException("Дата релиза должна быть указана");
    }
    if (film.getReleaseDate().isBefore(FIRST_FILM_DATE)) {
        throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
    }
    if (film.getDuration() == null || film.getDuration() <= 0) {
        throw new ValidationException("Продолжительность фильма должна быть положительным числом");
    }
}
}