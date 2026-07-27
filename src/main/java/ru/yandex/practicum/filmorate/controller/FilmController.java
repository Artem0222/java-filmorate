package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Validated
@Slf4j
@RequestMapping("/films")
@RestController
public class FilmController {

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
    public Film findById(@PathVariable @Positive Long id) {
        return filmStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.save(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            throw new ru.yandex.practicum.filmorate.exception.ValidationException("id должен быть указан");
        }
        if (!filmStorage.existsById(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        return filmStorage.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        log.info("Пользователь {} ставит лайк фильму {}", userId, id);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        log.info("Пользователь {} удаляет лайк у фильма {}", userId, id);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(required = false) Integer count) {
        log.info("Запрос популярных фильмов, count={}", count);
        return filmService.getPopularFilms(count);
    }
}