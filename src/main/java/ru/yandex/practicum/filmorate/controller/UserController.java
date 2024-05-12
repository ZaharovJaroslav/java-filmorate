package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private int id;


    private int setId() {
        return ++id;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        int lastId = setId();
        if (user.getId() <= id) {
            user.setId(lastId);
        } else {
            id = user.getId();
        }
        log.info("Создание пользователя");
        User newUser = validationUser(user);
        users.put(id, newUser);
        log.info("Пользователь {} успешно создан", newUser.getName());
        return newUser;
    }

    @PutMapping
    public User upadateUser(@RequestBody User user) {
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            log.info("Обновление пользователя: id = {}; name = {}",oldUser.getId(), oldUser.getName());
            oldUser = validationUser(user);
            log.info("Пользователь обновлен: id = {}; name = {}", oldUser.getId(), oldUser.getName());
            return oldUser;
        }
        log.warn("Ошибка обновления пользователя: id = {}; name = {}", user.getId(), user.getName());
        throw new NotFoundException("Пользователь с id = " +  user.getId() + " не найден");
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
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
