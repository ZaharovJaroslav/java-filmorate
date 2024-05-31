package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.storage.film.FilmStorage.TOP_10_FILMS;
@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    @Autowired
    public FilmService(UserService userService) {
        this.filmStorage = new InMemoryFilmStorage();
        this.userService = userService;
    }

    public Film addFilm(Film film) {
       return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilmById(int id) {
        filmStorage.deleteFilmById(id);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addLike(int filmId, int userId) {
        log.info("Доабвление <Like> к фильму");
        if (filmId <= 0 || userId <= 0) {
            throw new ValidationException("id пользователя не может быть меньше значния <1>");
        }

        Film film = filmStorage.getFilms()
                .stream()
                .filter(film1 -> film1.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + "не найден"));

        if (film.getLiks().contains(userId)) {
            throw new ValidationException("Пользователь c id = " + userId + " уже ставил <LIKE> фильму с id = " + filmId);
        }
        User user = userService.userSearchById(userId);
        film.getLiks().add(userId);
        log.info("Пользвоатель {} поставил <Like> фильму {} ",user.getLogin(), film.getName());

        return film;
    }

    public void deleteLikeById(int filmId, int userId) {
        log.info("Удаление <Like> пользвоателя из фильма");
        if (filmId <= 0 || userId <= 0) {
            throw new ValidationException("id пользователя не может быть меньше значния <1>");
        }
        Film film = filmStorage.getFilms()
                .stream()
                .filter(film1 -> film1.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + "не найден"));

        if (!film.getLiks().contains(userId)) {
            throw new NotFoundException("Ошибка удаления <Like>: Пользователь с id = " + userId +
                                        "не ставил <Like> фильму с id = " + filmId);
        } else
            film.getLiks().remove(userId);
        log.info("<Like> удален");
    }

    public Collection<Film> getPopularMoviesByLikes(int count) {
        log.info("Получение списка популярных фильмов по количеству <Like>");
        log.debug("Количество фильмов для получения -<{}>", count);
        List<Film> popularMovies = new ArrayList<>();
        Collection<Film> requestedCountMovies = new ArrayList<>();

        if (getFilms().isEmpty()) {
            return popularMovies;
        }
        popularMovies = getFilms()
                .stream()
                .sorted(Comparator.comparingInt(film -> film.getLiks().size())).collect(Collectors.toList()).reversed();

        if (count != 0 && count > popularMovies.size()) {
            log.debug("Количество фильмов для получения <{}>", count);
            for (int i = 0; i < popularMovies.size(); i++) {
                if (i < count) {
                    requestedCountMovies.add(popularMovies.get(i));
                }
            }
        } else {
            log.debug("Количество фильмов для получения не задано - возвращаем Топ - 10");
            for (int i = 0; i < popularMovies.size(); i++) {
                if (i < TOP_10_FILMS) {
                    requestedCountMovies.add(popularMovies.get(i));
                }
            }
        }
        return requestedCountMovies;
    }
}
