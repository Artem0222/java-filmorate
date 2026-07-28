package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;


public interface FilmStorage {
    Collection<Film> findAll();

    Optional<Film> findById(Long id);

    Film save(Film film);

    Film update(Film film);

    void deleteById(Long id);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    boolean existsById(Long id);

    default boolean mpaExists(int mpaId) {
        return true;
    }

    default boolean genreExists(int genreId) {
        return true;
    }
}
