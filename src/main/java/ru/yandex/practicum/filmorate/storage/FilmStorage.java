package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.Optional;


public interface FilmStorage {
    Collection<Film> findAll();
    Optional<Film> findById(Long id);
    Film save(Film film);
    Film update(Film film);
    void deleteByID(Long id);
    boolean existsById(Long id);
}
