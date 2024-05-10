package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Film.
 */
@Data
public class Film {
    int id;
    String name;
    String description;
    LocalDate releaseDate;
    long duration;

}
