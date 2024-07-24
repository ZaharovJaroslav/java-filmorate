package ru.yandex.practicum.filmorate.storage.db.filmReviewRating;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmReviewRating;

@Slf4j
@Component("FilmReviewRatingDbStorage")
@RequiredArgsConstructor
public class FilmReviewRatingDbStorage implements FilmReviewRatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void create(FilmReviewRating filmReviewRating) {
        jdbcTemplate.update(
                "MERGE INTO film_review_ratings(review_id, user_id, rating) KEY (review_id, user_id) VALUES (?, ?, ?)",
                filmReviewRating.getReviewId(),
                filmReviewRating.getUserId(),
                filmReviewRating.getRating());
    }

    @Override
    public void remove(FilmReviewRating filmReviewRating) {
        jdbcTemplate.update("DELETE FROM film_review_ratings WHERE review_id = ? AND user_id = ?",
                filmReviewRating.getReviewId(),
                filmReviewRating.getUserId());
        log.trace("FilmReviewRatingDbStorage::remove success");
    }
}