package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private int id;

    private int setId() {
        return ++id;
    }

    @Override
    public User createUser(User user) {
        int lastId = setId();
        if (user.getId() <= id) {
            user.setId(lastId);
            log.debug("Генерация и присвоение уникального id пользователю. id = {}", id);
        } else {
            id = user.getId();
        }
        log.info("Создание пользователя");
        log.debug("Валидация фильма c id = {}", user.getId());
        User newUser = validationUser(user);
        users.put(id, newUser);
        log.info("Пользователь {} успешно создан", newUser.getName());
        return newUser;
    }

    @Override
    public User updateUser(User user) {
        log.info("Обновление пользователя");
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            log.debug("Обновление пользователя: id = {}; name = {}",oldUser.getId(), oldUser.getName());
            oldUser = validationUser(user);
            log.info("Пользователь уcпешно обновлен");
            return oldUser;
        }
        log.warn("Ошибка обновления пользователя: id = {}; name = {}", user.getId(), user.getName());
        throw new NotFoundException("Пользователь с id = " +  user.getId() + " не найден");
    }

    public Collection<User> getUsers() {
        log.info("Получение списка всех пользовтелей");
        return users.values();
    }

    @Override
    public void deleteUserById(int id) {
        log.info("Удаление пользователя");
        if (users.containsKey(id)) {
            users.remove(id);
            log.info("Пользователь успешно удален");
        } else {
            log.warn("Ошибка удаления пользователя: id = {}", id);
            throw new NotFoundException("Пользователь с id = " + id + " не существует");
        }
    }

    public User validationUser(User user) {
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
}


