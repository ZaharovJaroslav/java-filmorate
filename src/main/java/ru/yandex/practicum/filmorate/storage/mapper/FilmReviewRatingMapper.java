package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FilmReviewRating;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmReviewRatingMapper implements RowMapper<FilmReviewRating> {

    @Override
    public FilmReviewRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmReviewRating filmReviewRating = new FilmReviewRating();
        filmReviewRating.setRatingId(rs.getLong("rating_id"));
        filmReviewRating.setReviewId(rs.getLong("review_id"));
        filmReviewRating.setUserId(rs.getLong("user_id"));
        filmReviewRating.setRating(rs.getInt("rating"));
        return filmReviewRating;
    }
}
