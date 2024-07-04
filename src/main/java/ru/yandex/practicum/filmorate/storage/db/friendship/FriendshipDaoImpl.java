package ru.yandex.practicum.filmorate.storage.db.friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.mapper.FriendshipMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FriendshipDaoImpl implements FriendshipDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId, boolean isFriend) {
        log.debug("addFriend({}, {}, {})", userId, friendId, isFriend);
        jdbcTemplate.update("INSERT INTO friends (user_id, friend_id, is_friend) VALUES(?, ?, ?)",
                userId, friendId, isFriend);
        Friendship friendship = getFriend(userId, friendId);
        log.trace("Теперь эти пользователи - друзья: {}", friendship);

    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        log.debug("deleteFriend({}, {})", userId, friendId);
        Friendship friendship = Objects.requireNonNull(getFriend(userId, friendId));
        jdbcTemplate.update("DELETE FROM friends WHERE user_id=? AND friend_id=?", userId, friendId);
        if (friendship.isFriend()) {
            jdbcTemplate.update("UPDATE friends SET is_friend=false WHERE user_id=? AND friend_id=?",
                    userId, friendId);
            log.debug("The friendship between {} and {} is over", userId, friendId);
        }
        log.trace("Теперь они не друзья: {}", friendship);
    }

    public List<Integer> getFriends(int userId) {
        log.debug("getFriends({})", userId);
        List<Integer> friendsList = jdbcTemplate.query(
                        "SELECT user_id, friend_id, is_friend FROM friends WHERE user_id=?",
                        new FriendshipMapper(), userId)
                .stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toList());
        return friendsList;
    }

    @Override
    public Friendship getFriend(int userId, int friendId) {
        log.debug("getFriend({}, {})", userId, friendId);
        return jdbcTemplate.queryForObject(
                "SELECT user_id, friend_id, is_friend FROM friends WHERE user_id=? AND friend_id=?",
                new FriendshipMapper(), userId, friendId);
    }

    @Override
    public boolean isFriend(int userId, int friendId) {
        log.debug("isFriend({}, {})", userId, friendId);
        try {
            getFriend(userId, friendId);
            log.trace("Нашел дружбу между {} и {}", userId, friendId);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("Никакой дружбы между {} и {} обнаружено не было", userId, friendId);
            return false;
        }
    }
}
