package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@ResponseBody
public class UserController {
    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody User user) {
        if (user == null) {
            throw new NotFoundException("Не указан пользователь для создания");
        }
        return userService.createUser(user);
    }

    @PutMapping("/users")
    public User upadateUser(@RequestBody User user) {
        if (user == null) {
            throw new NotFoundException("Не указан пользователь для обновления");
        }
        return userService.updateUser(user);
    }

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") int id) {
        return userService.userSearchById(id);
    }

    @DeleteMapping("/users/{id}")
    public String deleteUserById(@PathVariable("id") int id) {
        userService.deleteUserById(id);
        return "Пользователь успешно удален";

    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public User addFriend(@PathVariable("id") int id,
                          @PathVariable("friendId") int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("users/{id}/friends/{friendId}")
    public User deleteFromFriends(@PathVariable("id") int id,
                                  @PathVariable("friendId") int friendId) {
        return userService.deleteFromFriends(id, friendId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable("id") int id,
                                             @PathVariable("otherId") int otherId) {
        return userService.getMutualFriends(id,otherId);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getUsersFriends(@PathVariable("id") int id) {
        return userService.getUsersFriends(id);
    }

}
