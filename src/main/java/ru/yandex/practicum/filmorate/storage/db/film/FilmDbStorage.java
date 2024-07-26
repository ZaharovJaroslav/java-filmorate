package ru.yandex.practicum.filmorate.storage.db.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.FilmFilter;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.sql.Date;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Component("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcOperations namedParameterJdbc;

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

        for (Director director : film.getDirectors()) {
            jdbcTemplate.update("INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)",
                    thisFilm.getId(), director.getId());
        }
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
    public List<Film> getRecommendedFilms(int userId, int commonUserId) {
        String findIdSql = "SELECT film_id FROM likes " +
                "WHERE user_id = ? AND film_id NOT IN (SELECT film_id FROM likes WHERE user_id = ?)";
        try {
            return jdbcTemplate.query(findIdSql,
                    ((rs, rowN) -> getFilmById(rs.getInt("film_id")).get()), commonUserId, userId);
        } catch (EmptyResultDataAccessException ignored) {
            return List.of();
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
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id=?", film.getId());
        for (Director director : film.getDirectors()) {
            jdbcTemplate.update("INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)",
                    film.getId(), director.getId());
        }
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
            List<Director> directors = jdbcTemplate.query(
                    "SELECT d.id, d.name FROM directors d JOIN film_directors fd ON fd.director_id = d.id WHERE fd.film_id = ?",
                    new DirectorMapper(), filmId);
            thisFilm.setDirectors(directors);

            return Optional.of(thisFilm);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Фильм с id {} не найден", filmId);
            return Optional.empty();
        }
    }

    @Override
    public void deleteFilmById(int id) {
        log.debug("deleteFilmById({})", id);
        Optional<Film> film = getFilmById(id);
        if (film.isPresent()) {
            jdbcTemplate.update("DELETE FROM films WHERE film_id = ?", id);
            log.trace("Фильм с id = {} удален", id);
        } else {
            log.debug("Не удалось удалить фильм с id = {}", id);
            throw new NotFoundException("Фильм с таким id не существует");
        }
    }

    @Override
    public Collection<Film> getFilms() {
        log.debug("getFilms()");
        List<Film> films = jdbcTemplate.query(
                "SELECT film_id, name, description, release_date, duration, mpa_id FROM films", new FilmMapper());
        log.trace("В базе данных есть фильмы: {}", films);
        for (Film film : films) {
            List<Director> directors = jdbcTemplate.query(
                    "SELECT d.id, d.name FROM directors d JOIN film_directors fd ON fd.director_id = d.id WHERE fd.film_id = ?",
                    new DirectorMapper(), film.getId());
            film.setDirectors(directors);
        }
        return films;
    }

    @Override
    public void addGenres(int filmId, Set<Genre> genres) {
        log.debug("addGenres({}, {})", filmId, genres);
        for (Genre genre : genres) {
            jdbcTemplate.update("INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)", filmId, genre.getId());
            log.trace("В фильм были добавлены жанры {}", filmId);
        }
    }

    @Override
    public void updateGenres(int filmId, Set<Genre> genres) {
        log.debug("updateGenres({}, {})", filmId, genres);
        deleteGenres(filmId);
        addGenres(filmId, genres);
    }

    @Override
    public Set<Genre> getGenres(int filmId) {
        log.debug("getGenres({})", filmId);
        List<Genre> genres = jdbcTemplate.query(
                "SELECT f.genre_id, g.genre_type FROM film_genre AS f " +
                        "LEFT OUTER JOIN genre AS g ON f.genre_id = g.genre_id WHERE f.film_id=? ORDER BY g.genre_id",
                new GenreMapper(), filmId);
        log.trace("Были возвращены жанры для фильма с идентификатором {}", filmId);
        return new HashSet<>(genres);
    }

    @Override
    public void deleteGenres(int filmId) {
        log.debug("deleteGenres({})", filmId);
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id=?", filmId);
        log.trace("Все жанры были удалены для фильма с идентификатором {}", filmId);
    }

    @Override
    public List<Film> getFilmsByDirector(long directorId) {
        String sql = "SELECT f.* FROM films f JOIN film_directors fd ON f.film_id = fd.film_id WHERE fd.director_id = ?";
        List<Film> films = jdbcTemplate.query(sql, new FilmMapper(), directorId);

        for (Film film : films) {
            film.setMpa(getMpaForFilm(film.getMpa().getId()));
            film.setGenres(getGenres(film.getId()));
            film.setDirectors(getDirectorsForFilm(film.getId()));
        }

        return films;
    }

    @Override
    public List<Film> searchFilms(String query, String[] by) {
        String likeQuery = "%" + query.toLowerCase() + "%";
        StringJoiner conditionsQuery = new StringJoiner(" OR ");

        for (String field : by) {
            if ("title".equalsIgnoreCase(field)) {
                conditionsQuery.add("LOWER(films.name) LIKE ?");
            } else if ("director".equalsIgnoreCase(field)) {
                conditionsQuery.add("LOWER(directors.name) LIKE ?");
            } else {
                throw new IllegalArgumentException("Неверное поле поиска: " + field);
            }
        }

        String sql = "SELECT films.film_id, films.name, films.description, films.release_date, films.duration, films.mpa_id " +
                "FROM films " +
                "LEFT JOIN film_directors ON films.film_id = film_directors.film_id " +
                "LEFT JOIN directors ON film_directors.director_id = directors.id " +
                (conditionsQuery.length() == 0 ? "" : "WHERE " + conditionsQuery) +
                "ORDER BY (SELECT COUNT(*) FROM likes WHERE film_id = films.film_id) DESC";

        Object[] params = Stream.of(by)
                .map(field -> likeQuery)
                .toArray();

        List<Film> films = jdbcTemplate.query(sql, params, new FilmMapper());

        for (Film film : films) {
            film.setGenres(getGenres(film.getId()));
            film.setDirectors(getDirectorsForFilm(film.getId()));
            film.setMpa(getMpaForFilm(film.getMpa().getId()));
        }

        return films;
    }

    private List<Director> getDirectorsForFilm(long filmId) {
        log.debug("getDirectorsForFilm({})", filmId);
        String sql = "SELECT d.* FROM directors d " +
                "JOIN film_directors fd ON d.id = fd.director_id " +
                "WHERE fd.film_id = ?";
        return jdbcTemplate.query(sql, new DirectorMapper(), filmId);
    }

    private Mpa getMpaForFilm(long mpaId) {
        log.debug("getMpaForFilm({})", mpaId);
        String sql = "SELECT * FROM film_mpa WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, new MpaMapper(), mpaId);
    }

    @Override
    public List<Film> findByFilter(int count, Map<FilmFilter, Optional<Integer>> filter) {
        StringJoiner conditionsQuery = new StringJoiner("AND ");
        Map<String, Integer> queryParams = new HashMap<>();
        queryParams.put(FilmFilter.COUNT.getValue(), count);

        if (filter.get(FilmFilter.YEAR).isPresent()) {
            queryParams.put(FilmFilter.YEAR.getValue(), filter.get(FilmFilter.YEAR).get());
            conditionsQuery.add("YEAR(films.release_date) = :" + FilmFilter.YEAR.getValue() + " ");
        }
        if (filter.get(FilmFilter.GENRE).isPresent()) {
            queryParams.put(FilmFilter.GENRE.getValue(), filter.get(FilmFilter.GENRE).get());
            conditionsQuery.add("film_genre.genre_id = :" + FilmFilter.GENRE.getValue() + " ");
        }

        String sql = "SELECT films.film_id, name, description, release_date, duration, mpa_id " +
                "FROM film_genre " +
                "RIGHT JOIN films on film_genre.film_id = films.film_id " +
                "LEFT JOIN likes ON films.film_id = likes.film_id " +
                (conditionsQuery.length() == 0 ? "" : "WHERE " + conditionsQuery) +
                "GROUP BY films.film_id ORDER BY COUNT(likes.user_id) DESC LIMIT :" + FilmFilter.COUNT.getValue();
        return namedParameterJdbc.query(sql, queryParams, new FilmMapper());
    }
}