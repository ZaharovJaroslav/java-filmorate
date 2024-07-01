package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotContentException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.friendship.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.db.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.db.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class  UserService {
    private final UserStorage userStorage;
    private final FriendshipDao friendshipDao;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserDbStorage userStorage,
                         FriendshipDao friendshipDao) {
        this.userStorage = userStorage;
        this.friendshipDao = friendshipDao;
    }


    public User createUser(User user) {
        log.debug("createUser");
        if (userStorage.isContains(user.getId())) {
            throw new ObjectAlreadyExistsException("Пользователь с id = " + user.getId() +  "уже существует");
        }
        User newUser =  validationUser(user);
        return userStorage.createUser(newUser);
    }

    public User updateUser(User user) {
        log.debug("updateUser");
        if (!userStorage.isContains(user.getId())) {
            throw new NotFoundException("Пользователь с id = " + user.getId() +  "не существует");
        }
        User updateUser = validationUser(user);
        return userStorage.updateUser(updateUser);
    }

    public void deleteUserById(int id) {
        log.debug("getGenreById({})", id);
        if (userStorage.isContains(id)) {
            throw new NotFoundException("Пользователь с id = " + id +  "не существует");
        }
        userStorage.deleteUserById(id);
    }

    public Collection<User> getUsers() {
        log.debug("getUsers()");
        return userStorage.getUsers();
    }

    public User getUserById(int userId) {
        log.debug("getGenreById({})", userId);
        if (userId <= 0) {
            throw new ValidationException("id пользователя не может быть меньше значния <1>");
        }
        if (!userStorage.isContains(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId +  "не существует");
        }

        return userStorage.getUsers()
                .stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new  NotFoundException("пользователь с id = " + userId + "не найден"));
    }

    public User validationUser(User user) {
        log.debug("validationUser");
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: email {}", user.getEmail());
            throw new ValidationException("электронная почта не может быть пустой и должна содержать символ <@>");
        }
        if (user.getLogin() != null && !user.getLogin().isBlank()) {
            String login = user.getLogin();
            for (int i = 0; i < login.length(); i++) {
                char ch = login.charAt(i);
                if (Character.isWhitespace(ch)) {
                    log.warn("Ошибка валидации: login {}", user.getEmail());
                    throw new ValidationException("огин не может содержать пробел");
                }
            }
        } else {
            log.warn("Ошибка валидации: login {}", user.getEmail());
            throw new ValidationException("логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: birthday {}", user.getBirthday());
            throw new ValidationException("дата рождения не может быть пустым, а так же не может быть в будущем");
        }
        return user;
    }


    public void addFriend(int userId, int newFriendId) {
        log.debug("addFriend({}, {})",userId, newFriendId);
        log.info("Добавление пользовтеля в друзья");
        validationUserId(userId,newFriendId);
        if (friendshipDao.isFriend(userId, newFriendId)) {
            throw new ValidationException("Пользователь с id = userId1  и userId2 уже дружат");
        }
        boolean isFriend = friendshipDao.isFriend(userId, newFriendId);
        friendshipDao.addFriend(userId, newFriendId, isFriend);
    }

    public void deleteFromFriends(int userId, int userToDeleteId) {
        log.debug("deleteFromFriends({}, {})",userId, userToDeleteId);
        log.debug(" Удаление друга у пользователя {} удаляемый друг -{}", userId, userToDeleteId);

        validationUserId(userId,userToDeleteId);
        if (!friendshipDao.isFriend(userId, userToDeleteId)) {
            throw new NotContentException("Пользователь с id = userId1  и userId2 не дружат");
        }
        friendshipDao.deleteFriend(userId,userToDeleteId);
    }

    public Collection<User> getMutualFriends(int userId, int friendId) {
        log.debug("getMutualFriends({}, {})",userId, friendId);
        validationUserId(userId, friendId);
        List<User> userFriends = getUsersFriends(userId);
        List<User> friendFriends = getUsersFriends(friendId);

        return friendFriends.stream()
                .filter(userFriends::contains)
                .filter(friendFriends::contains)
                .collect(Collectors.toList());
    }

    public List<User> getUsersFriends(int userId) {
        log.debug("getUsersFriends");

        if (!userStorage.isContains(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId +  "не существует");
        }
        List<User> friends = friendshipDao.getFriends(userId).stream()
                .mapToInt(Integer::valueOf)
                .mapToObj(userStorage::getUserById)
                .collect(Collectors.toList());
        return friends;
    }

    public void validationUserId(int userId1, int userId2) {
        log.debug("validationUserId({}, {})",userId1, userId2);
        log.debug("Валидация id пользвоателей  userId1 = {}, userId2 = {}", userId1, userId2);
        if (userId1 <= 0 || userId2 <= 0) {
            throw new ValidationException("id пользователя не может быть меньше значния <1>");
        }
        if (userId1 == userId2) {
            throw new ValidationException("id пользоватлей не могут быть одинаковыми");
        }
        if (!userStorage.isContains(userId1)) {
            throw new NotFoundException("Пользователь с id = " + userId1 +  "не существует");
        }
        if (!userStorage.isContains(userId2)) {
            throw new NotFoundException("Пользователь с id = " + userId2 +  "не существует");
        }
    }
}
