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
        FilmReview filmReview = new FilmReview();
        filmReview.setReviewId(reviewId);
        filmReview.setFilmId(filmId);
        filmReview.setUserId(userId);
        filmReview.setContent(content);
        filmReview.setIsPositive(isPositive);
        return filmReview;
    }
}
