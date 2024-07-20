package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.EventOperation;
import ru.yandex.practicum.filmorate.enums.UserEventType;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.storage.db.userEvent.UserEventStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class UserEventService {
    private final UserEventStorage userEventStorage;

    @Autowired
    public UserEventService(@Qualifier("UserEventDbStorage") UserEventStorage userEventStorage) {
        this.userEventStorage = userEventStorage;
    }

    /**
     * Получить все события друзей пользователя
     *
     * @param userId    ИД пользователя
     * @return          Список событий друзей
     */
    public Collection<UserEvent> getByUserFriends(long userId) {
        return userEventStorage.getByUserFriends(userId);
    }

    /**
     * Создать событие: "Добавление в друзья"
     *
     * @param userId   ИД пользователя
     * @param entityId ИД пользователя-друга
     */
    public void addFriendEvent(long userId, long entityId) {
        UserEvent userEvent = new UserEvent(userId, UserEventType.FRIEND, EventOperation.ADD, entityId);
        logEvent(userEvent, userEventStorage.create(userEvent));
    }

    /**
     * Создать событие: "Удаление из друзей"
     *
     * @param userId   ИД пользователя
     * @param entityId ИД пользователя-друга
     */
    public void deleteFriendEvent(long userId, long entityId) {
        UserEvent userEvent = new UserEvent(userId, UserEventType.FRIEND, EventOperation.REMOVE, entityId);
        logEvent(userEvent, userEventStorage.create(userEvent));
    }

    /**
     * Создать событие: "Добавлен лайк для фильма"
     *
     * @param userId   ИД пользователя
     * @param entityId ИД пользователя-друга
     */
    public void addLikeEvent(long userId, long entityId) {
        UserEvent userEvent = new UserEvent(userId, UserEventType.LIKE, EventOperation.ADD, entityId);
        logEvent(userEvent, userEventStorage.create(userEvent));
    }

    /**
     * Создать событие: "Удален лайк для фильма"
     *
     * @param userId   ИД пользователя
     * @param entityId ИД пользователя-друга
     */
    public void dislikeEvent(long userId, long entityId) {
        UserEvent userEvent = new UserEvent(userId, UserEventType.LIKE, EventOperation.REMOVE, entityId);
        logEvent(userEvent, userEventStorage.create(userEvent));
    }

    /**
     * Создать событие: "Добавлен отзыв фильма"
     *
     * @param userId   ИД пользователя
     * @param entityId ИД фильма
     */
    public void createFilmReviewEvent(long userId, long entityId) {
        UserEvent userEvent = new UserEvent(userId, UserEventType.REVIEW, EventOperation.ADD, entityId);
        logEvent(userEvent, userEventStorage.create(userEvent));
    }

    /**
     * Создать событие: "Изменен отзыв фильма"
     *
     * @param userId   ИД пользователя
     * @param entityId ИД фильма
     */
    public void updateFilmReviewEvent(long userId, long entityId) {
        UserEvent userEvent = new UserEvent(userId, UserEventType.REVIEW, EventOperation.UPDATE, entityId);
        logEvent(userEvent, userEventStorage.create(userEvent));
    }

    /**
     * Создать событие: "Удален отзыв фильма"
     *
     * @param userId   ИД пользователя
     * @param entityId ИД фильма
     */
    public void removeFilmReviewEvent(long userId, long entityId) {
        UserEvent userEvent = new UserEvent(userId, UserEventType.REVIEW, EventOperation.REMOVE, entityId);
        logEvent(userEvent, userEventStorage.create(userEvent));
    }

    private void logEvent(UserEvent userEvent, Optional<UserEvent> userEventOptional) {
        if (userEventOptional.isEmpty()) {
            log.warn("Не удалось создать событие <{} - {}> для пользователя <{}>. Объект: <{}>", userEvent.getEventType(), userEvent.getOperation(), userEvent.getUserId(), userEvent.getEntityId());
            return;
        }
        log.debug("Создано событие <{} - {}> для пользователя <{}>. Объект: <{}>", userEvent.getEventType(), userEvent.getOperation(), userEvent.getUserId(), userEvent.getEntityId());
    }
}
