package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.UserEventType;
import ru.yandex.practicum.filmorate.model.UserEvent;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserEventMapper implements RowMapper<UserEvent> {

    @Override
    public UserEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserEvent userEvent = new UserEvent();
        userEvent.setEventId(rs.getLong("event_id"));
        userEvent.setUserId(rs.getLong("user_id"));
        userEvent.setEventType(UserEventType.valueOf(rs.getString("event_type")));
        userEvent.setOperation(EventOperation.valueOf(rs.getString("operation")));
        userEvent.setEntityId(rs.getLong("entity_id"));
        userEvent.setTimestamp(rs.getTimestamp("timestamp"));
        return userEvent;
    }
}
