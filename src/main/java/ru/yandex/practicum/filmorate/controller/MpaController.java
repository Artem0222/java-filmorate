package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRepository;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaController {

    private final MpaRepository mpaRepository;

    @Autowired
    public MpaController(MpaRepository mpaRepository) {
        this.mpaRepository = mpaRepository;
    }

    @GetMapping
    public List<MpaRating> getAllMpa() {
        return mpaRepository.findAll();
    }

    @GetMapping("/{id}")
    public MpaRating getMpaById(@PathVariable int id) {
        return mpaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + " не найден"));
    }
}