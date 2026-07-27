package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@AutoConfigureTestDatabase
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;

    @Test
    void testSaveAndFindById() {
        User user = createTestUser();
        User saved = userStorage.save(user);

        Optional<User> found = userStorage.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getEmail(), found.get().getEmail());
    }

    @Test
    void testFindAll() {
        userStorage.save(createTestUser());
        userStorage.save(createTestUser2());

        Collection<User> users = userStorage.findAll();
        assertFalse(users.isEmpty());
        assertTrue(users.size() >= 2);
    }

    @Test
    void testAddFriend() {
        User user1 = userStorage.save(createTestUser());
        User user2 = userStorage.save(createTestUser2());

        userStorage.addFriend(user1.getId(), user2.getId());

        Collection<User> friends = userStorage.getFriends(user1.getId());
        assertFalse(friends.isEmpty());
        assertEquals(1, friends.size());
    }

    private User createTestUser() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    private User createTestUser2() {
        User user = new User();
        user.setEmail("test2@test.com");
        user.setLogin("testuser2");
        user.setName("Test User 2");
        user.setBirthday(LocalDate.of(1991, 1, 1));
        return user;
    }
}
