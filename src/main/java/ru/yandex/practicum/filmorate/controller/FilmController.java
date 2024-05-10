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
    final static int DESCRIPTION_LENGTH = 200;
    final static LocalDate MOVIE_BIRTHDAY = LocalDate.of(1895, Month.DECEMBER,28);
    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Добалвение фильма");
        Film newFilm = validationFilm(film);
        films.put(getNextId(), newFilm);
        log.info("Фильм {} успешно добавлен", newFilm.getName());
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            Film oldfilm  = films.get(film.getId());
            log.info("Обновление пользователя: id = {}; name = {}", oldfilm.getId(), oldfilm.getName());
            Film newFilm = validationFilm(film);
            oldfilm.setName(newFilm.getName());
            oldfilm.setDescription(newFilm.getDescription());
            oldfilm.setReleaseDate(newFilm.getReleaseDate());
            oldfilm.setDuration(newFilm.getDuration());
            log.info("Пользователь обновлен: id = {}; name = {}", oldfilm.getId(), oldfilm.getName());
            return oldfilm;
        }
        log.warn("Ошибка обновления пользователя: id = {}; name = {}", film.getId(), film.getName());
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    public Film validationFilm(Film film) {
        if (film.getId() == 0) {
            film.setId(getNextId());
        }
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > DESCRIPTION_LENGTH) {
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }
        return film;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    public int getNextId() {
        int currentMaId = films.keySet()
                .stream()
                .mapToInt(id ->id)
                .max()
                .orElse(0);
        return ++currentMaId;
    }
}
