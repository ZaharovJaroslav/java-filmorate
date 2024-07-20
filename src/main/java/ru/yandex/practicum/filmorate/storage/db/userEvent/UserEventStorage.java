package ru.yandex.practicum.filmorate.storage.db.userEvent;

import ru.yandex.practicum.filmorate.model.UserEvent;

import java.util.Collection;
import java.util.Optional;

public interface UserEventStorage {
    Collection<UserEvent> getByUser(long userId);

    Collection<UserEvent> getByUserFriends(long userId);

    Optional<UserEvent> create(UserEvent userEvent);

}
