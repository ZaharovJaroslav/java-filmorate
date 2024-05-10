package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
public class User {
    int id;
    String email;
    String login;
    String name;
    LocalDate birthday;
}
