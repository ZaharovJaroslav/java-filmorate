package ru.yandex.practicum.filmorate.storage.db.filmReview;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmReview;

import java.util.Collection;
import java.util.Optional;

public interface FilmReviewStorage {

    Collection<FilmReview> getFilmReviews();

    Optional<FilmReview> find(long id);

    Optional<FilmReview> create(FilmReview filmReview);

    Optional<FilmReview> update(FilmReview filmReview);

    void remove(long id);
}
