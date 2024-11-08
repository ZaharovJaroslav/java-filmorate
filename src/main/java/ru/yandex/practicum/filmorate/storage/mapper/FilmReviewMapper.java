package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.FilmReview;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmReviewMapper implements RowMapper<FilmReview> {

    @Override
    public FilmReview mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmReview filmReview = new FilmReview();
        filmReview.setReviewId(rs.getLong("review_id"));
        filmReview.setFilmId(rs.getLong("film_id"));
        filmReview.setUserId(rs.getLong("user_id"));
        filmReview.setContent(rs.getString("content"));
        filmReview.setIsPositive(rs.getBoolean("is_positive"));
        filmReview.setUseful(rs.getInt("useful"));
        return filmReview;
    }
}
