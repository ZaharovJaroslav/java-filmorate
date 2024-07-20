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
    public Collection<UserEvent> get(long userId) {
        List<UserEvent> userEvents = jdbcTemplate.query("SELECT * FROM user_events " +
                "WHERE user_id = ?", new UserEventMapper(), userId);
        log.trace("UserEventDbStorage::get success: {}", userEvents);
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