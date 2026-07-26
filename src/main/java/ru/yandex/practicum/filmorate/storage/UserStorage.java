package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    User update(User user);

    void deleteById(Long id);

    boolean existsById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<Long> getFriendIds(Long userId);
}