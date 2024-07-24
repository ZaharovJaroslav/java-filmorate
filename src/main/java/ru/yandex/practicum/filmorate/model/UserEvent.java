package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.UserEventType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent {
    private long eventId;
    private long userId;
    private UserEventType eventType;
    private EventOperation operation;
    private long entityId;
    private long timestamp;

    public UserEvent(long userId, UserEventType eventType, EventOperation operation, long entityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }
}
