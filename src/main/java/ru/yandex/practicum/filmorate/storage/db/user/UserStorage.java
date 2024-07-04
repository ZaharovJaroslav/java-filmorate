package ru.yandex.practicum.filmorate.storage.db.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    void deleteUserById(int id);

    Collection<User> getUsers();

    Optional<User> getUserById(int id);

    void checkNotExsistUser(int userId);

}
