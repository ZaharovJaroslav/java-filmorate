package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FilmReviewRating.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmReviewRating {
    private long ratingId;
    private long reviewId;
    private long userId;
    private int rating;

    public FilmReviewRating(long reviewId, long userId) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.rating = 1;
    }

    public FilmReviewRating(long reviewId, long userId, int rating) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.rating = rating;
    }
}
