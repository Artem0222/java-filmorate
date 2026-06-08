package ru.yandex.practicum.filmorate.storage;

import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long nextId = 1L;


}
