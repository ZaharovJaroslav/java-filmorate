package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @NonNull
    private int id;
    private String name;

    public Genre(int id) {
        this.id = id;
    }
}