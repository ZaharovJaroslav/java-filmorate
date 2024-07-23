package ru.yandex.practicum.filmorate.storage.db.Like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.mapper.LikeMapper;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDaoImpl implements LikeDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void like(int filmId, int userId) {
        log.debug("like({}, {})", filmId, userId);
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
        log.trace("Фильм {} понравился пользователю {}", filmId, userId);
    }

    @Override
    public void dislike(int filmId, int userId) {
        log.debug("dislike({}, {})", filmId, userId);
        jdbcTemplate.update("DELETE FROM likes WHERE film_id=? AND user_id=?", filmId, userId);
        log.trace("Пользователю {}, которому не понравился фильм {}", userId, filmId);
    }

    @Override
    public int countLikes(int filmId) {
        log.debug("countLikes({}).", filmId);
        Integer count = Objects.requireNonNull(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id=?", Integer.class, filmId));
        log.trace("Фильм {} понравился {} раз", filmId, count);
        return count;
    }

    @Override
    public boolean isLiked(int filmId, int userId) {
        log.debug("isLiked({}, {})", filmId, userId);
        try {
            jdbcTemplate.queryForObject("SELECT film_id, user_id FROM likes WHERE film_id=? AND user_id=?",
                    new LikeMapper(), filmId, userId);
            log.trace("Фильм {} понравился пользователю {}", filmId, userId);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("Нет лайка на фильм {} от пользователя {}", filmId, userId);
            return false;
        }
    }

    @Override
    public Optional<Collection<Like>> getAllLikesUser(int userId) {
        log.debug("getAllLikesUser({})", userId);
        return Optional.of(jdbcTemplate.query("SELECT* FROM likes WHERE user_id=?",
                new LikeMapper(), userId));
    }
}
