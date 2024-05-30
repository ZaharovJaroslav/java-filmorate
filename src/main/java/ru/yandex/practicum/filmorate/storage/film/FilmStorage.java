package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

public interface FilmStorage {
    int TOP_10_FILMS = 10;
    int DESCRIPTION_LENGTH = 200;
    LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER,28);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilmById(int id);

    Collection<Film> getFilms();

}
