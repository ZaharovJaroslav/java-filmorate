package ru.yandex.practicum.filmorate.storage.db.Like;

import ru.yandex.practicum.filmorate.model.Like;

import java.util.Collection;
import java.util.Optional;

public interface LikeDao {
    void like(int filmId, int userId);

    void dislike(int filmId, int userId);

    int countLikes(int filmId);

    boolean isLiked(int filmId, int userId);

    Optional<Collection<Like>> getAllLikesUser(int userId);
}
