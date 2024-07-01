package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {
    @NonNull
    private int id;
    @NonNull
    private String name;

    public Mpa(int id) {
        this.id = id;
    }
}
