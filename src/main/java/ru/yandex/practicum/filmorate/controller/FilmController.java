package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.*;


@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    @ResponseStatus(HttpStatus.CREATED)
    public Film addFilm(@RequestBody Film film) {
        if (film == null) {
            throw new NotFoundException("Не указан фильм для создания");
        }
        return filmService.addFilm(film);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        if (film == null) {
            throw new NotFoundException("Не указан фильм для обновления");
        }
        return filmService.updateFilm(film);
    }

    @GetMapping("/films")
    protected Collection<Film> getFilms() {
        return filmService.getFilms();
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLike(@PathVariable("id") int id,
                        @PathVariable("userId") int userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public String deleteLikeById(@PathVariable("id") int id,
                                 @PathVariable("userId") int userId) {
        filmService.deleteLikeById(id, userId);
        return "<Like> Успешно удален";
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopularMoviesByLikes(@RequestParam(name = "count", required = false) String count) {
        if (count.isBlank()) {
            return filmService.getPopularMoviesByLikes(0);
        }
        return filmService.getPopularMoviesByLikes(Integer.parseInt(count));
    }

}
