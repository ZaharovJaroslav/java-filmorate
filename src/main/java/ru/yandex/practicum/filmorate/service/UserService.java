package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.exception.NotContentException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.friendship.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.db.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.db.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FriendshipDao friendshipDao;
    private final FilmService filmService;
    private final UserEventService userEventService;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserDbStorage userStorage,
                       FriendshipDao friendshipDao,
                       FilmService filmService,
                       UserEventService userEventService) {
        this.userStorage = userStorage;
        this.friendshipDao = friendshipDao;
        this.filmService = filmService;
        this.userEventService = userEventService;
    }

    public User createUser(User user) {
        log.debug("createUser");
        Optional<User> thisUser = userStorage.getUserById(user.getId());
        if (thisUser.isEmpty()) {
            User newUser = validationUser(user);
            return userStorage.createUser(newUser);
        } else
            throw new ObjectAlreadyExistsException("Пользователь с id = " + user.getId() + "уже существует");
    }

    public User updateUser(User user) {
        log.debug("updateUser");
        checkNotExsistUser(user.getId());
        User userToUpdate = validationUser(user);

        return userStorage.updateUser(userToUpdate);

    }

    public void deleteUserById(int userId) {
        log.debug("getGenreById({})", userId);
        checkNotExsistUser(userId);
        userStorage.deleteUserById(userId);
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
        Optional<User> thisUser = userStorage.getUserById(userId);
        if (thisUser.isPresent()) {
            return thisUser.get();
        } else
            throw new NotFoundException("Пользователь с id = " + userId + "не существует");

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
                    throw new ValidationException("логин не может содержать пробел");
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
        log.debug("addFriend({}, {})", userId, newFriendId);
        checkNotExsistUser(userId);
        checkNotExsistUser(newFriendId);
        validationUserId(userId, newFriendId);

        boolean isFriend = friendshipDao.isFriend(userId, newFriendId);
        if (isFriend) {
            throw new ValidationException("Пользователь с id = userId1  и userId2 уже дружат");
        } else
            friendshipDao.addFriend(userId, newFriendId, isFriend);

        userEventService.addFriendEvent(userId, newFriendId);
    }

    public void deleteFromFriends(int userId, int userToDeleteId) {
        log.debug("deleteFromFriends({}, {})", userId, userToDeleteId);
        validationUserId(userId, userToDeleteId);
        checkNotExsistUser(userId);
        checkNotExsistUser(userToDeleteId);

        if (!friendshipDao.isFriend(userId, userToDeleteId)) {
            throw new NotContentException("Пользователь с id = userId1  и userId2 не дружат");
        }
        friendshipDao.deleteFriend(userId, userToDeleteId);

        userEventService.deleteFriendEvent(userId, userToDeleteId);
    }

    public Collection<User> getMutualFriends(int userId, int friendId) {
        log.debug("getMutualFriends({}, {})", userId, friendId);
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
        checkNotExsistUser(userId);

        List<User> friends = friendshipDao.getFriends(userId).stream()
                .mapToInt(Integer::valueOf)
                .mapToObj(userStorage::getUserById)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        return friends;
    }

    public void validationUserId(int userId1, int userId2) {
        log.debug("validationUserId({}, {})", userId1, userId2);
        log.debug("Валидация id пользвоателей  userId1 = {}, userId2 = {}", userId1, userId2);
        if (userId1 <= 0 || userId2 <= 0) {
            throw new ValidationException("id пользователя не может быть меньше значния <1>");
        }
        if (userId1 == userId2) {
            throw new ValidationException("id пользоватлей не могут быть одинаковыми");
        }
    }

    public void checkNotExsistUser(int userId) {
        Optional<User> thisUser = userStorage.getUserById(userId);
        if (thisUser.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + "не существует");
        }
    }

    public List<Film> getUsersRecommendations(int id) {
        Optional<Integer> userId = userStorage.findUserWithMaxCommonLikes(id);
        if (userId.isPresent()) {
            return filmService.getRecommendedFilms(id, userId.get());
        } else
            return List.of();
    }
}
