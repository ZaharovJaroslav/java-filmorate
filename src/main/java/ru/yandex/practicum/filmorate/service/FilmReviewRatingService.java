
package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.model.FilmReviewRating;
import ru.yandex.practicum.filmorate.storage.db.filmReview.FilmReviewStorage;
import ru.yandex.practicum.filmorate.storage.db.filmReviewRating.FilmReviewRatingStorage;

import java.util.Optional;

@Slf4j
@Service
public class FilmReviewRatingService {
    private final FilmReviewRatingStorage filmReviewRatingStorage;
    private final FilmReviewStorage filmReviewStorage;

    @Autowired
    public FilmReviewRatingService(@Qualifier("FilmReviewRatingDbStorage") FilmReviewRatingStorage filmReviewRatingStorage, @Qualifier("FilmReviewDbStorage") FilmReviewStorage filmReviewStorage) {
        this.filmReviewRatingStorage = filmReviewRatingStorage;
        this.filmReviewStorage = filmReviewStorage;
    }

    public FilmReview addLike(long reviewId, long userId) {
        FilmReviewRating filmReviewRating = new FilmReviewRating(reviewId, userId);
        filmReviewRatingStorage.create(filmReviewRating);
        return getFilmReview(reviewId);
    }

    public FilmReview removeLike(long reviewId, long userId) {
        FilmReviewRating filmReviewRating = new FilmReviewRating(reviewId, userId);
        filmReviewRatingStorage.remove(filmReviewRating);
        return getFilmReview(reviewId);
    }

    public FilmReview addDislike(long reviewId, long userId) {
        FilmReviewRating filmReviewRating = new FilmReviewRating(reviewId, userId, -1);
        filmReviewRatingStorage.create(filmReviewRating);
        return getFilmReview(reviewId);
    }

    public FilmReview removeDislike(long reviewId, long userId) {
        FilmReviewRating filmReviewRating = new FilmReviewRating(reviewId, userId);
        filmReviewRatingStorage.remove(filmReviewRating);
        return getFilmReview(reviewId);
    }

    public FilmReview getFilmReview(long reviewId) {
        Optional<FilmReview> filmReviewOptional = filmReviewStorage.find(reviewId);
        if (filmReviewOptional.isEmpty()) {
            throw new NotFoundException("Оценка для фильма не найдена");
        }
        return filmReviewOptional.get();
    }
}
