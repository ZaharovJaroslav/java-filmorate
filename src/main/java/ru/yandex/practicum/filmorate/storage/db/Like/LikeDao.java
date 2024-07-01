package ru.yandex.practicum.filmorate.storage.db.Like;

public interface LikeDao {
    void like(int filmId, int userId);

    void dislike(int filmId, int userId);

    int countLikes(int filmId);

    boolean isLiked(int filmId, int userId);
}
