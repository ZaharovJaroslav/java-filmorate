package ru.yandex.practicum.filmorate.storage.db.friendship;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;

public interface FriendshipDao {
    void addFriend(int userId, int friendId, boolean isFriend);

    void deleteFriend(int userId, int friendId);

    Collection<Integer> getFriends(int userId);

    Friendship getFriend(int userId, int friendId);

    boolean isFriend(int userId, int friendId);
}
