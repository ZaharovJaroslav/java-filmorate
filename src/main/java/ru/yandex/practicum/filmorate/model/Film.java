package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


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
    Set<Integer> liks = new HashSet<>();
}
