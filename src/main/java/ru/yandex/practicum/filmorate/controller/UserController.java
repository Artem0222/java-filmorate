package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {


    private final Map<Long, User> users = new HashMap<>();


    private Long nextId = 1L;


    @GetMapping
    public Collection<User> findAll() {
        log.info("Запрос на получение всех пользователей. Количество: {}", users.size());
        return users.values();
    }


    @PostMapping
    public User create(@RequestBody User user) {
        log.info("Получен запрос на создание пользователя: {}", user);


        validateUser(user);


        user.setId(nextId++);
        users.put(user.getId(), user);

        log.info("Пользователь успешно создан с id = {}", user.getId());
        return user;
    }


    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("Получен запрос на обновление пользователя: {}", newUser);


        if (newUser.getId() == null) {
            log.warn("Попытка обновления пользователя без указания id");
            throw new ValidationException("id должен быть указан для обновления");
        }


        if (!users.containsKey(newUser.getId())) {
            log.warn("Пользователь с id = {} не найден для обновления", newUser.getId());
            throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден");
        }


        validateUser(newUser);


        users.put(newUser.getId(), newUser);

        log.info("Пользователь с id = {} успешно обновлён", newUser.getId());
        return newUser;
    }


    private void validateUser(User user) {
        // Проверка email
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.warn("Ошибка валидации: email пустой");
            throw new ValidationException("Электронная почта не может быть пустой");
        }

        if (!user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: email '{}' не содержит символ @", user.getEmail());
            throw new ValidationException("Электронная почта должна содержать символ @");
        }


        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Ошибка валидации: логин пустой");
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: логин '{}' содержит пробелы", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }


        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя пустое, используем логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }


        if (user.getBirthday() == null) {
            log.warn("Ошибка валидации: дата рождения не указана");
            throw new ValidationException("Дата рождения должна быть указана");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: дата рождения {} в будущем", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        log.debug("Валидация пользователя пройдена успешно: {}", user.getLogin());
    }
}