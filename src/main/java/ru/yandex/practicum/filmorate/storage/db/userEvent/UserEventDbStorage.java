package ru.yandex.practicum.filmorate.storage.db.userEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.storage.mapper.UserEventMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component("UserEventDbStorage")
@RequiredArgsConstructor
public class UserEventDbStorage implements UserEventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<UserEvent> getByUser(long userId) {
        List<UserEvent> userEvents = jdbcTemplate.query("SELECT * FROM user_events " +
                "WHERE user_id = ? ORDER BY event_id", new UserEventMapper(), userId);
        log.trace("UserEventDbStorage::getByUser success: {}", userEvents);
        return userEvents;
    }

    @Override
    public Collection<UserEvent> getByUserFriends(long userId) {
        List<UserEvent> userEvents = jdbcTemplate.query("SELECT * FROM USER_EVENTS WHERE USER_ID IN " +
                "(SELECT USER_ID FROM FRIENDS WHERE FRIEND_ID = ? AND IS_FRIEND = true " +
                "UNION SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ? AND IS_FRIEND = true) " +
                "ORDER BY TIMESTAMP DESC", new UserEventMapper(), userId, userId);
        log.trace("UserEventDbStorage::getByUserFriends success: {}", userEvents);
        return userEvents;
    }

    @Override
    public Optional<UserEvent> create(UserEvent userEvent) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO user_events(user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, userEvent.getUserId());
            ps.setString(2, userEvent.getEventType().name());
            ps.setString(3, userEvent.getOperation().name());
            ps.setLong(4, userEvent.getEntityId());
            return ps;
        }, keyHolder);
        try {
            long id = (Long) Objects.requireNonNull(keyHolder.getKeys()).get("event_id");
            userEvent.setEventId(id);
            log.trace("UserEventDbStorage::create success: {}", userEvent);
            return Optional.of(userEvent);
        } catch (NullPointerException exception) {
            log.trace("UserEventDbStorage::create error: " + exception.getMessage());
            return Optional.empty();
        }
    }
}
