package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FilmReview.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmReview {
    private long reviewId;
    private long userId;
    private long filmId;
    private String content;
    private boolean isPositive;
    private int useful;

    public FilmReview(long userId, long filmId, String content, boolean isPositive) {
        this.userId = userId;
        this.filmId = filmId;
        this.content = content;
        this.isPositive = isPositive;
    }

    public FilmReview(long reviewId, long userId, long filmId, String content, boolean isPositive) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.filmId = filmId;
        this.content = content;
        this.isPositive = isPositive;
    }

    public boolean getIsPositive() {
        return this.isPositive;
    }

    public void setIsPositive(boolean isPositive) {
        this.isPositive = isPositive;
    }
}
