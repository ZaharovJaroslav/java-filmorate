package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
     private final UserStorage userStorage;


    @Autowired
    public UserService() {
        this.userStorage = new InMemoryUserStorage();
    }

    public User createUser(User user) {
      return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void deleteUserById(int id) {
        userStorage.deleteUserById(id);
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addFriend(int userId, int newFriendId) {
        log.info("Добавление пользовтеля в друзья");
        User user = userSearchById(userId);
        User newFriend = userSearchById(newFriendId);

        if (user.getFriends().contains(newFriendId) && newFriend.getFriends().contains(userId)) {
            throw new ValidationException("У пользователя уже есть дргу с таким id");
        }
        user.getFriends().add(newFriendId);
        newFriend.getFriends().add(userId);
        log.info("Пользовтель <{}> добавил в друзья пользователя <{}>", user.getName(), newFriend.getName());

        return user;
    }

    public User deleteFromFriends(int userId, int userToDeleteId) {
        log.info("Удаление пользователя из списка друзей");
        User user = userSearchById(userId);
        User userToDelete = userSearchById(userToDeleteId);

        user.getFriends().remove(userToDeleteId);
        userToDelete.getFriends().remove(userId);
        log.info("Пользователь <{}> удалил из друзей пользователя <{}>", user.getName(), userToDelete.getName());

        return user;
    }

    public Collection<User> getMutualFriends(int userId, int friendId) {
        log.info("Получение списка общих друзей пользователей");
        validationUserId(userId, friendId);

        Collection<User> mutualFriends = new ArrayList<>();
        User user = userSearchById(userId);
        User friend = userSearchById(friendId);

        if ((user.getFriends() == null || user.getFriends().isEmpty()) || (friend.getFriends() == null
                                                                       || friend.getFriends().isEmpty())) {
            throw new ValidationException("У пользователя нет друзей");
        }

        log.debug("Формирование списка c id общих друзей пользователей с id = {} и id = {}", user.getId(), friend.getId());
        List<Integer> mutualFriendsId = user.getFriends()
                .stream()
                .filter(x -> friend.getFriends().contains(x))
                .collect(ArrayList::new, List::add, List::addAll);

        Collection<User> allUsers = userStorage.getUsers();
        log.debug("Формирование списка общих друзей");
        for (User us : allUsers) {
            if (mutualFriendsId.contains(us.getId())) {
                mutualFriends.add(us);
            }
        }
        log.info("Список общих друзей пользователей <{}> и <{}> успешно сформирован,", user.getName(), friend.getName());
        return mutualFriends;
    }

    public Collection<User> getUsersFriends(int userId) {
        log.info("Получение всех друзей пользователя");
        Collection<User> userFriends = new ArrayList<>();
        User user = userSearchById(userId);

        Collection<User> allUsers = userStorage.getUsers();

        log.debug("Формирование списка друзей пользователя с id = <{}>", user.getId());
        for (User us : allUsers) {
            if (user.getFriends().contains(us.getId()) && us.getFriends().contains(userId)) {
                userFriends.add(us);
            }
        }
        log.info("Списко друзей пользователя <{}> успешно сформирован", user.getName());
        return userFriends;
    }

    public User userSearchById(int userId) {
            log.debug("Поиск пользоватлея с id = <{}>", userId);
            if (userId <= 0) {
                throw new ValidationException("id пользователя не может быть меньше значния <1>");
            }

            return userStorage.getUsers()
                    .stream()
                    .filter(user -> user.getId() == userId)
                    .findFirst()
                    .orElseThrow(() -> new  NotFoundException("пользователь с id = " + userId + "не найден"));
    }

    public void validationUserId(int userId1, int userId2) {
        log.debug("Валидация id пользвоателей userId1 = {}, userId2 = {}", userId1, userId2);
        if (userId1 <= 0 || userId2 <= 0) {
            throw new ValidationException("id пользователя не может быть меньше значния <1>");
        }
        if (userId1 == userId2) {
            throw new ValidationException("id пользоватлей не могут быть одинаковыми");
        }
        log.debug("Валидация прошла успешно");
    }
}
