
package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.Like.LikeDao;
import ru.yandex.practicum.filmorate.storage.db.directors.DirectorDao;
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
    private final DirectorDao directorDao;

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage,
                       GenreDao genreDao,
                       MpaDao mpaDao,
                       LikeDao likeDao,
                       DirectorDao directorDao) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.likeDao = likeDao;
        this.directorDao = directorDao;
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
            newFilm.setDirectors(getDirectorsByIds(film.getDirectors()));
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
        thisFilm.setDirectors(getDirectorsByIds(film.getDirectors()));
        return thisFilm;
    }

    public Film getFilmById(int filmId) {
        log.debug("getFilmById");
        Optional<Film> film = filmStorage.getFilmById(filmId);
        if (film.isPresent()) {
            Film thisFilm = film.get();
            Set<Genre> genres = new HashSet<>(filmStorage.getGenres(filmId));
            thisFilm.setGenres(genres.stream().toList());
            thisFilm.setMpa(mpaDao.getMpaById(thisFilm.getMpa().getId()));
            setDirectorsForFilm(thisFilm);
            return thisFilm;
        } else
            throw new NotFoundException("Фильм с таким id не существует");
    }

    public void deleteFilmById(int id) {
        log.debug("deleteFilmById({})", id);
        getFilmById(id);
        filmStorage.deleteFilmById(id);

    }

    public Collection<Film> getFilms() {
        log.debug("getFilms");
        var films = filmStorage.getFilms();
        for (Film film : films) {
            film.setGenres(filmStorage.getGenres(film.getId()));
            film.setMpa(mpaDao.getMpaById(film.getMpa().getId()));
            setDirectorsForFilm(film);
        }
        return films;
    }

    public Collection<Film> getCommonFilmsSortedByPopular(int userId, int friendId) {
        log.debug("getCommonFilmsSortedByPopular({},{})",userId, friendId);
        Collection<Film> films = new ArrayList<>();
        checkNotExsistUser(userId);
        checkNotExsistUser(friendId);
        Optional<Collection<Like>> userLikes = likeDao.getAllLikesUser(userId);
        Optional<Collection<Like>> friendLikes = likeDao.getAllLikesUser(friendId);

        if (userLikes.isPresent() && friendLikes.isPresent()) {
            List<Integer> userFilmsId = userLikes.get().stream()
                    .mapToInt(Like::getFilmId)
                    .boxed()
                    .toList();

            return films = friendLikes.get().stream()
                    .filter(like -> userFilmsId.contains(like.getFilmId()))
                    .map(like -> getFilmById(like.getFilmId()))
                    .sorted(this::compare)
                    .collect(Collectors.toList());
        } else
            return films;
    }

    public List<Genre> getGenresFilm(int filmId) {
        log.debug("getGenresFilm");
        filmStorage.getFilmById(filmId);
        return filmStorage.getGenres(filmId);
    }

    public Collection<Film> getPopularMoviesByLikes(int count) {
        log.debug("getPopularMoviesByLikes({})", count);
        List<Film> popularMovies = getFilms()
                .stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toList());
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

    public List<Film> getFilmsByDirector(long directorId, String sortBy) {
        List<Film> films = filmStorage.getFilmsByDirector(directorId);
        if ("year".equalsIgnoreCase(sortBy)) {
            return films.stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate))
                    .collect(Collectors.toList());
        } else if ("likes".equalsIgnoreCase(sortBy)) {
            return films.stream()
                    .sorted((film1, film2) -> Integer.compare(likeDao.countLikes(film2.getId()), likeDao.countLikes(film1.getId())))
                    .collect(Collectors.toList());
        }
        return films;
    }

    private void likeChecker(int filmId, int userId) {
        log.debug("likeChecker({}, {})", filmId, userId);
        filmStorage.getFilmById(filmId);
        userStorage.checkNotExsistUser(userId);
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

    public void checkNotExsistUser(int userId) {
        Optional<User> thisUser = userStorage.getUserById(userId);
        if (thisUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + "не существует");
        }
    }

    private List<Director> getDirectorsByIds(List<Director> directors) {
        List<Director> foundDirectors = new ArrayList<>();
        for (Director director : directors) {
            Director foundDirector = directorDao.getDirectorById(director.getId())
                    .orElseThrow(() -> new NotFoundException("Режиссер с id=" + director.getId() + " не найден."));
            foundDirectors.add(foundDirector);
        }
        return foundDirectors;
    }

    private void setDirectorsForFilm(Film film) {
        List<Director> directors = directorDao.getDirectorsByFilmId(film.getId());
        film.setDirectors(directors);
    }

}
