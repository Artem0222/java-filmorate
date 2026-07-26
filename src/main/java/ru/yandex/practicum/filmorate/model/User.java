package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.time.LocalDate;


@Data
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();


}
