package ru.yandex.practicum.filmorate.storage.db.film;

import ru.yandex.practicum.filmorate.enums.FilmFilter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FilmStorage {
    int DESCRIPTION_LENGTH = 200;
    LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER,28);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilmById(int filmId);

    void deleteFilmById(int id);

    Collection<Film> getFilms();

    void addGenres(int filmId, List<Genre> genres);

    void updateGenres(int filmId, List<Genre> genres);

    List<Genre> getGenres(int filmId);

    void deleteGenres(int filmId);

    Optional<Film> checkForRepeat(Film film);

    List<Film> getRecommendedFilms(int userId, int commonUserId);

    List<Film> findByFilter(int count, Map<FilmFilter, Optional<Integer>> filter);

    List<Film> getFilmsByDirector(long directorId);

    List<Film> searchFilms(String query, String[] by);
}
