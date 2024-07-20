
package ru.yandex.practicum.filmorate.controller.FilmReview;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.service.FilmReviewRatingService;


@Slf4j
@RestController
@RequestMapping("/reviews/{id}")
public class FilmReviewRatingController {
    private final FilmReviewRatingService filmReviewRatingService;

    @Autowired
    public FilmReviewRatingController(FilmReviewRatingService filmReviewRatingService) {
        this.filmReviewRatingService = filmReviewRatingService;
    }

    @PutMapping("/like/{userId}")
    public FilmReview addLike(@PathVariable long id, @PathVariable long userId) {
        return filmReviewRatingService.addLike(id, userId);
    }

    @DeleteMapping("/like/{userId}")
    public FilmReview removeLike(@PathVariable long id, @PathVariable long userId) {
        return filmReviewRatingService.removeLike(id, userId);
    }

    @PutMapping("/dislike/{userId}")
    public FilmReview addDislike(@PathVariable long id, @PathVariable long userId) {
        return filmReviewRatingService.addDislike(id, userId);
    }

    @DeleteMapping("/dislike/{userId}")
    public FilmReview removeDislike(@PathVariable long id, @PathVariable long userId) {
        return filmReviewRatingService.removeDislike(id, userId);
    }
}

