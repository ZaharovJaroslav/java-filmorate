package ru.yandex.practicum.filmorate.storage.db.filmReviewRating;

import ru.yandex.practicum.filmorate.model.FilmReviewRating;

public interface FilmReviewRatingStorage {

    void create(FilmReviewRating filmReview);

    void remove(FilmReviewRating filmReviewRating);
}
