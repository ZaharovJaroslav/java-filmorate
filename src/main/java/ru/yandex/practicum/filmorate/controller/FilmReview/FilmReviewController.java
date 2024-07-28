package ru.yandex.practicum.filmorate.controller.FilmReview;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final CreateFilmReviewValidator createFilmReviewValidator;
    private final UpdateFilmReviewValidator updateFilmReviewValidator;

    @Autowired
    public FilmReviewController(FilmReviewService filmReviewService,
                                @Qualifier("CreateFilmReviewValidator") CreateFilmReviewValidator createFilmReviewValidator,
                                @Qualifier("UpdateFilmReviewValidator") UpdateFilmReviewValidator updateFilmReviewValidator) {
        this.filmReviewService = filmReviewService;
        this.createFilmReviewValidator = createFilmReviewValidator;
        this.updateFilmReviewValidator = updateFilmReviewValidator;
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
        createFilmReviewValidator.validate(request);
        if (!createFilmReviewValidator.isValid()) {
            throw new ValidationException("Невалидные параметры", createFilmReviewValidator.getMessages());
        }
        return filmReviewService.create(request);
    }

    @PutMapping
    public FilmReview update(@RequestBody FilmReviewRequest request) {
        updateFilmReviewValidator.validate(request);
        if (!updateFilmReviewValidator.isValid()) {
            throw new ValidationException("Невалидные параметры", updateFilmReviewValidator.getMessages());
        }
        return filmReviewService.update(request);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable long id) {
        filmReviewService.remove(id);
    }
}

