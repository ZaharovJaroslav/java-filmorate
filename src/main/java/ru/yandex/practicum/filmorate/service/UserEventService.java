package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.storage.db.userEvent.UserEventStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserEventService {
    private final UserEventStorage userEventStorage;

    @Autowired
    public UserEventService(@Qualifier("UserEventDbStorage") UserEventStorage userEventStorage) {
        this.userEventStorage = userEventStorage;
    }

    public Collection<UserEvent> get(long userId) {
        return userEventStorage.get(userId);
    }
}
