
package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmById(id);
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
    public void addLike(@PathVariable("id") int id,
                        @PathVariable("userId") int userId) {
        filmService.addLike(id, userId);
    }

    @GetMapping("/films/popular")
    public Collection<Film> getPopularMoviesByLikes(@RequestParam(defaultValue = "10") int count,
                                                    @RequestParam Optional<Integer> genreId,
                                                    @RequestParam Optional<Integer> year) {
        return filmService.getPopularMoviesByLikes(count, genreId, year);
    }

    @GetMapping("/films/id")
    Collection<Genre> getGenresFilm(@PathVariable int id) {
        return filmService.getGenresFilm(id);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.dislike(id, userId);
    }

    @GetMapping("/films/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable long directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/films/search")
    public ResponseEntity<List<Film>> searchFilms(@RequestParam String query, @RequestParam String by) {
        String[] searchFields = by.split(",");
        List<Film> films = filmService.searchFilms(query, searchFields);
        return ResponseEntity.ok(films);
    }

    @DeleteMapping("/films/{id}")
    public void deleteFilmById(@PathVariable("id") int id) {
        filmService.deleteFilmById(id);
    }

}

