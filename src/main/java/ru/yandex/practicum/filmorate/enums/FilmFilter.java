package ru.yandex.practicum.filmorate.enums;

import lombok.Getter;

@Getter
public enum FilmFilter {
    COUNT("count"), YEAR("year"), GENRE("genreId");

    private final String value;

    FilmFilter(String value) {
        this.value = value;
    }
}
