
package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.Like.LikeDao;
import ru.yandex.practicum.filmorate.storage.db.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.db.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.db.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDao;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.db.film.FilmStorage.*;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final LikeDao likeDao;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       GenreDao genreDao,
                       MpaDao mpaDao,
                       LikeDao likeDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.likeDao = likeDao;
    }

    public Film addFilm(Film film) {
        log.debug("addFilm(({})", film);
        checkIfExists(film);
        validationFilm(film);
        Set<Genre> genres = new HashSet<>(film.getGenres());

        Optional<Film> thisFilm = filmStorage.checkForRepeat(film);
        if (thisFilm.isPresent()) {
            Film filmUpdated = thisFilm.get();
            filmStorage.updateGenres(filmUpdated.getId(), genres.stream().toList());
            filmUpdated.setGenres(filmStorage.getGenres(filmUpdated.getId()));
            return filmUpdated;
        } else {
            Film newFilm = filmStorage.addFilm(film);
            filmStorage.addGenres(newFilm.getId(), genres.stream().toList());
            newFilm.setGenres(filmStorage.getGenres(newFilm.getId()));
            newFilm.setMpa(mpaDao.getMpaById(newFilm.getMpa().getId()));
            return newFilm;
        }
    }

    public Film updateFilm(Film film) {
        log.debug("updateFilm");
        checkIfExists(film);
        validationFilm(film);
        Film thisFilm = filmStorage.updateFilm(film);
        filmStorage.updateGenres(thisFilm.getId(), film.getGenres());
        thisFilm.setGenres(filmStorage.getGenres(thisFilm.getId()));
        thisFilm.setMpa(mpaDao.getMpaById(thisFilm.getMpa().getId()));
        return thisFilm;
    }

    public Film getFilmById(int filmId) {
        log.debug("getFilmById");
        Film film = filmStorage.getFilmById(filmId);
        Set<Genre> genres = new HashSet<>(filmStorage.getGenres(filmId));
        film.setGenres(genres.stream().toList());
        film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
        return film;
    }

    public Collection<Film> getFilms() {
        log.debug("getFilms");
        var films = filmStorage.getFilms();
        for (Film film : films) {
            film.setGenres(filmStorage.getGenres(film.getId()));
            film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
        }
        return films;
    }

    private List<Film> fillFilms(List<Film> films) {
        for (Film film : films) {
            film.setGenres(filmStorage.getGenres(film.getId()));
            film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
        }
        return films;
    }

    public List<Genre> getGenresFilm(int filmId) {
        log.debug("getGenresFilm");
        filmStorage.getFilmById(filmId);
        return filmStorage.getGenres(filmId);
    }

    public Collection<Film> getPopularMoviesByLikes(int count, Optional<Integer> genreId, Optional<Integer> year) {
        log.debug("getPopularMoviesByLikes({})", count);
        List<Film> popularMovies;
        if (genreId.isPresent() && year.isPresent()) {
            popularMovies = fillFilms(filmStorage.findByGenreYear(count, genreId.get(), year.get()));
        } else if (genreId.isPresent()) {
            popularMovies = fillFilms(filmStorage.findByGenre(count, genreId.get()));
        } else if (year.isPresent()) {
            popularMovies = fillFilms(filmStorage.findByYear(count, year.get()));
        } else {
            popularMovies = getFilms()
                    .stream()
                    .sorted(this::compare)
                    .limit(count)
                    .collect(Collectors.toList());
        }
        return popularMovies;
    }

    private int compare(Film film, Film otherFilm) {
        return Integer.compare(likeDao.countLikes(otherFilm.getId()), likeDao.countLikes(film.getId()));
    }

    public void addLike(int filmId, int userId) {
        log.debug("addLike({}, {})", filmId, userId);
        likeChecker(filmId, userId);
        if (likeDao.isLiked(filmId, userId)) {
            throw new NotFoundException("Пользователю с идентификатором " + userId + " уже понравился фильм" + filmId);
        }
        likeDao.like(filmId, userId);
    }

    public void dislike(int filmId, int userId) {
        log.debug("dislike({}, {})", filmId, userId);
        likeChecker(filmId, userId);
        if (!likeDao.isLiked(filmId, userId)) {
            throw new NotFoundException("Пользователю с идентификатором " + userId + " не понравился фильм" + filmId);
        }
        likeDao.dislike(filmId, userId);
    }

    private void likeChecker(int filmId, int userId) {
        log.debug("likeChecker({}, {})", filmId, userId);
        filmStorage.getFilmById(filmId);
        userStorage.checkNotExsistUser(userId);
    }

    public void validationFilm(Film film) {
        log.debug("validationFilm");
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка валидации: название фильма - <{}>", film.getName());
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()
                || film.getDescription().length() > DESCRIPTION_LENGTH) {
            log.warn("Ошибка валидации: описание фильма - <{}>", film.getDescription());
            throw new ValidationException("Описание не может быть путстым, максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            log.warn("Ошибка валидации: дата релиза фильма - <{}>", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть пустым, а так же раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка валидации: продолжительность фильма - <{}>", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом " +
                    "а так же не может быть меньше 1 секунды");
        }
    }

    private void checkIfExists(Film film) {
        log.debug("checkIfExists");
        if (!mpaDao.isContains(film.getMpa().getId())) {
            throw new ValidationException("Не найден MPA для фильма с идентификатором" + film.getId());
        }
        for (Genre genre : film.getGenres()) {
            if (!genreDao.isContains(genre.getId())) {
                throw new ValidationException("Не удается найти жанр фильма с идентификатором" + genre.getId());
            }
        }
    }
}
