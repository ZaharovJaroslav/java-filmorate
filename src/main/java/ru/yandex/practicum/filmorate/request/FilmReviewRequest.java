package ru.yandex.practicum.filmorate.request;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.FilmReview;

@Data
public class FilmReviewRequest {
    private Long reviewId;
    private Long filmId;
    private Long userId;
    private String content;
    private Boolean isPositive;

    public FilmReview toFilmReview() {
        return reviewId != null
                ? new FilmReview(reviewId, userId, filmId, content, isPositive)
                : new FilmReview(userId, filmId, content, isPositive);
    }
}
