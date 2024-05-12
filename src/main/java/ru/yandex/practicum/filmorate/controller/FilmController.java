package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final int DESCRIPTION_LENGTH = 200;
    private static final LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER,28);
    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int id;


    private int setId() {
        return ++id;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        int lastId = setId();
        if (film.getId() <= lastId) {
            film.setId(lastId);
        } else {
            id = film.getId();
        }
        log.info("Добалвение фильма");
        Film newFilm = validationFilm(film);
        films.put(lastId, newFilm);
        log.info("Фильм {} успешно добавлен", newFilm.getName());
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            Film oldfilm  = films.get(film.getId());
            log.info("Обновление пользователя: id = {}; name = {}", oldfilm.getId(), oldfilm.getName());
            oldfilm = validationFilm(film);
            log.info("Пользователь обновлен: id = {}; name = {}", oldfilm.getId(), oldfilm.getName());
            return oldfilm;
        }
        log.warn("Ошибка обновления пользователя: id = {}; name = {}", film.getId(), film.getName());
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    public Film validationFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()
                                          || film.getDescription().length() > DESCRIPTION_LENGTH) {
            throw new ValidationException("Описание не может быть путстым, максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            throw new ValidationException("Дата релиза не может быть пустым, а так же раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом " +
                                          "а так же не может быть меньше 1 секунды");
        }
        return film;
    }

    @GetMapping
    protected Collection<Film> getFilms() {
        return films.values();
    }

}
