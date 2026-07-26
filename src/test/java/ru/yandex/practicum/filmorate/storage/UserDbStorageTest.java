package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.User;


import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    @Test
    void testFindAll() {
        Collection<User> users = userStorage.findAll();
        assertThat(users).isNotNull();
    }

    @Test
    void testSaveAndFindById() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User saved = userStorage.save(user);

        Optional<User> found = userStorage.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void testAddFriend() {
        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setLogin("user1");
        user1.setName("User One");
        user1.setBirthday(LocalDate.of(1990, 1, 1));

        User user2 = new User();
        user2.setEmail("user2@test.com");
        user2.setLogin("user2");
        user2.setName("User Two");
        user2.setBirthday(LocalDate.of(1991, 1, 1));

        User saved1 = userStorage.save(user1);
        User saved2 = userStorage.save(user2);

        userStorage.addFriend(saved1.getId(), saved2.getId());

        Collection<Long> friends = userStorage.getFriendIds(saved1.getId());
        assertThat(friends).contains(saved2.getId());
    }
}