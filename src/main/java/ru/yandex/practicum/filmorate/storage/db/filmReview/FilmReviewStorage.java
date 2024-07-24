package ru.yandex.practicum.filmorate.storage.db.filmReview;

import ru.yandex.practicum.filmorate.model.FilmReview;

import java.util.Collection;
import java.util.Optional;

public interface FilmReviewStorage {

    Collection<FilmReview> getFilmReviews(int count);

    Collection<FilmReview> getFilmReviewsByFilm(long filmId, int count);

    Optional<FilmReview> find(long id);

    Optional<FilmReview> create(FilmReview filmReview);

    Optional<FilmReview> update(FilmReview filmReview);

    void remove(long id);
}
