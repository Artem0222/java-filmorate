package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController();
    }

    @Test
    void shouldCreateValidUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user123");
        user.setName("Иван");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        User created = controller.create(user);

        assertNotNull(created.getId());
        assertEquals("user@example.com", created.getEmail());
    }

    @Test
    void shouldNotCreateUserWithBlankEmail() {
        User user = new User();
        user.setEmail("");
        user.setLogin("user123");
        user.setName("Иван");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldNotCreateUserWithEmailMissingAtSymbol() {
        User user = new User();
        user.setEmail("user.example.com");
        user.setLogin("user123");
        user.setName("Иван");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldNotCreateUserWithBlankLogin() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("");
        user.setName("Иван");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldNotCreateUserWithLoginContainingSpaces() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user 123");
        user.setName("Иван");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        assertThrows(ValidationException.class, () -> controller.create(user));
    }

    @Test
    void shouldUseLoginAsNameIfNameIsBlank() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("cool_user");
        user.setName("");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        User created = controller.create(user);

        assertEquals("cool_user", created.getName());
    }

    @Test
    void shouldUseLoginAsNameIfNameIsNull() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("cool_user");
        user.setName(null);
        user.setBirthday(LocalDate.of(1990, 5, 15));

        User created = controller.create(user);

        assertEquals("cool_user", created.getName());
    }

    @Test
    void shouldNotCreateUserWithBirthdayInFuture() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setLogin("user123");
        user.setName("Иван");
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> controller.create(user));
    }
}