package ru.yandex.practicum.filmorate.storage.db.filmReview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.storage.mapper.FilmReviewMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component("FilmReviewDbStorage")
@RequiredArgsConstructor
public class FilmReviewDbStorage implements FilmReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<FilmReview> getFilmReviews() {
        List<FilmReview> filmReviews = jdbcTemplate.query("SELECT review_id, film_id, user_id, content, is_positive FROM film_reviews " +
                "JOIN (SELECT SUM(rating) as useful, review_id FROM film_review_ratings GROUP BY review_id) as ratings " +
                "ON ratings.review_id = film_reviews.review_id", new FilmReviewMapper());
        log.trace("FilmReviewDbStorage::getFilmReviews success: {}", filmReviews);
        return filmReviews;
    }

    @Override
    public Optional<FilmReview> find(long id) {
        try {
            FilmReview filmReview = jdbcTemplate.queryForObject("SELECT review_id, film_id, user_id, content, is_positive FROM film_reviews " +
                            "JOIN (SELECT SUM(rating) as useful, review_id FROM film_review_ratings GROUP BY review_id) as ratings " +
                            "ON ratings.review_id = film_reviews.review_id " +
                            "WHERE review_id = ?",
                    new FilmReviewMapper(), id);
            log.trace("FilmReviewDbStorage::find success: {}", filmReview);
            return Optional.ofNullable(filmReview);
        } catch (EmptyResultDataAccessException e) {
            log.trace("Не удалось найти отзыв с ИД = " + id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<FilmReview> create(FilmReview filmReview) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO film_reviews(film_id, user_id, content, is_positive) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setObject(0, filmReview.getFilmId());
            ps.setObject(1, filmReview.getUserId());
            ps.setObject(2, filmReview.getContent());
            ps.setObject(3, filmReview.getIsPositive());
            return ps;
        }, keyHolder);
        try {
            long id = (Long) Objects.requireNonNull(keyHolder.getKeys()).get("ID");
            Optional<FilmReview> filmReviewOptional = this.find(id);
            if (filmReviewOptional.isPresent()) {
                log.trace("FilmReviewDbStorage::create success: {}", filmReviewOptional.get());
            } else {
                log.trace("FilmReviewDbStorage::create empty");
            }
            return filmReviewOptional;
        } catch (NullPointerException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<FilmReview> update(FilmReview filmReview) {
        jdbcTemplate.update(
                "UPDATE film_reviews SET film_id = ?, user_id = ?, content = ?, is_positive = ? WHERE review_id = ?",
                filmReview.getFilmId(),
                filmReview.getUserId(),
                filmReview.getContent(),
                filmReview.getIsPositive(),
                filmReview.getReviewId());
        Optional<FilmReview> filmReviewOptional = this.find(filmReview.getReviewId());
        if (filmReviewOptional.isPresent()) {
            log.trace("FilmReviewDbStorage::update success: {}", filmReviewOptional.get());
        } else {
            log.trace("FilmReviewDbStorage::update empty");
        }
        return filmReviewOptional;
    }

    @Override
    public void remove(long id) {
        jdbcTemplate.update("DELETE FROM film_reviews WHERE review_id = ?", id);
        log.trace("FilmReviewDbStorage::remove success");
    }
}