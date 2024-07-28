package ru.yandex.practicum.filmorate.controller.FilmReview;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.request.FilmReviewRequest;
import ru.yandex.practicum.filmorate.service.FilmReviewService;
import ru.yandex.practicum.filmorate.validator.CreateFilmReviewValidator;
import ru.yandex.practicum.filmorate.validator.UpdateFilmReviewValidator;

import java.util.Collection;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/reviews")
public class FilmReviewController {
    private final FilmReviewService filmReviewService;

    CreateFilmReviewValidator validator;

    @Autowired
    public FilmReviewController(FilmReviewService filmReviewService, CreateFilmReviewValidator validator) {
        this.filmReviewService = filmReviewService;
        this.validator = validator;
    }

    @GetMapping
    protected Collection<FilmReview> getFilmReviews(@RequestParam Optional<Long> filmId, @RequestParam(defaultValue = "10") Integer count) {
        return filmReviewService.getFilmReviews(filmId, count);
    }

    @GetMapping("/{id}")
    public FilmReview get(@PathVariable long id) {
        return filmReviewService.find(id);
    }

    @PostMapping
    public FilmReview create(@RequestBody FilmReviewRequest request) {
        validator.setRequest(request);
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Невалидные параметры", validator.getMessages());
        }
        return filmReviewService.create(request);
    }

    @PutMapping
    public FilmReview update(@RequestBody FilmReviewRequest request) {
        validator.setRequest(request);
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Невалидные параметры", validator.getMessages());
        }
        return filmReviewService.update(request);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable long id) {
        filmReviewService.remove(id);
    }
}

