package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;



/**
 * Film.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
   private int id;
   @NonNull
   private String name;
   private String description;
   private LocalDate releaseDate;
   private long duration;
    @NonNull
   private Mpa mpa;
   private List<Genre> genres = new ArrayList<>();


    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }
}
