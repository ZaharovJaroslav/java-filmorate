package ru.yandex.practicum.filmorate.storage.db.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    void deleteUserById(int id);

    Collection<User> getUsers();

    User getUserById(int id);

    boolean isContains(int id);

}
