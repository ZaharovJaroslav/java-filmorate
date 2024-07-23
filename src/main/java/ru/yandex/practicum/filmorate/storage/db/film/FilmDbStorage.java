package ru.yandex.practicum.filmorate.storage.db.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;

import java.sql.Date;
import java.util.*;

@Slf4j
@Component("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;


    @Override
    public Film addFilm(Film film) {
        log.debug("addFilm({})", film);
        jdbcTemplate.update(
                "INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());
        Film thisFilm = jdbcTemplate.queryForObject(
                "SELECT film_id, name, description, release_date, duration, mpa_id FROM films WHERE name=? "
                        + "AND description=? AND release_date=? AND duration=? AND mpa_id=?",
                new FilmMapper(), film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());
        log.trace("Фильм {} был добавлен в базу данных", thisFilm);
        return thisFilm;
    }

    public Optional<Film> checkForRepeat(Film film) {
        try {
            Film thisFilm = jdbcTemplate.queryForObject(
                    "SELECT film_id, name, description, release_date, duration, mpa_id FROM films WHERE name=? "
                            + "AND description=? AND release_date=? AND duration=? AND mpa_id=?",
                    new FilmMapper(), film.getName(),
                    film.getDescription(),
                    Date.valueOf(film.getReleaseDate()),
                    film.getDuration(),
                    film.getMpa().getId());
            return Optional.ofNullable(thisFilm);

        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("updateFilm({}).", film);
        jdbcTemplate.update(
                "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE film_id=?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
       Optional<Film> thisFilm = getFilmById(film.getId());
        if (thisFilm.isPresent()) {
            log.trace("Фильм {} обновлен в базе данных", thisFilm);
            return thisFilm.get();
        } else {
            log.trace("не удалось обновить фильм с id {}", film.getId());
            throw new NotFoundException("Фильм с таким id не существует");
        }
    }

    @Override
    public Optional<Film> getFilmById(int filmId) {
        log.debug("getFilmById({})", filmId);
        try {
            Film thisFilm = jdbcTemplate.queryForObject(
                    "SELECT film_id, name, description, release_date, duration, mpa_id FROM films WHERE film_id=?",
                    new FilmMapper(), filmId);
            log.trace("Фильм {} был возвращен", thisFilm);
            return Optional.ofNullable(thisFilm);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteFilmById(int id) {
        log.debug("deleteFilmById({})", id);
        jdbcTemplate.update("DELETE FROM films WHERE film_id =?", id);

        if (getFilmById(id).isPresent()) {
            log.trace("Фильм с id = {} удален",id);
        } else
            log.debug("Не удалось удалить фильм  с id = {}", id);
    }

    @Override
    public Collection<Film> getFilms() {
        log.debug("getFilms()");
        List<Film> films = jdbcTemplate.query(
                "SELECT film_id, name, description, release_date, duration, mpa_id FROM films", new FilmMapper());
        log.trace("В базе данных есть фильмы: {}", films);
        return films;
    }

    @Override
    public void addGenres(int filmId, List<Genre> genres) {
        log.debug("addGenres({}, {})", filmId, genres);
        for (Genre genre : genres) {
            jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", filmId, genre.getId());
            log.trace("В фильм были добавлены жанры {}", filmId);
        }
    }

    @Override
    public void updateGenres(int filmId, List<Genre> genres) {
        log.debug("updateGenres({}, {})", filmId, genres);
        deleteGenres(filmId);
        addGenres(filmId, genres);
    }

    @Override
    public List<Genre> getGenres(int filmId) {
        log.debug("getGenres({})", filmId);
        List<Genre> genres = new ArrayList<>(jdbcTemplate.query(
                "SELECT f.genre_id, g.genre_type FROM film_genre AS f " +
                        "LEFT OUTER JOIN genre AS g ON f.genre_id = g.genre_id WHERE f.film_id=? ORDER BY g.genre_id",
                new GenreMapper(), filmId));
        log.trace("Были возвращены жанры для фильма с идентификатором {}", filmId);
        return genres;
    }

    @Override
    public void deleteGenres(int filmId) {
        log.debug("deleteGenres({})", filmId);
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id=?", filmId);
        log.trace("Все жанры были удалены для фильма с идентификатором {}", filmId);
    }
}