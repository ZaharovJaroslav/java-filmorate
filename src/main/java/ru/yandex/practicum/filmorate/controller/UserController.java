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

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return userService.getUsers();
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

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable("id") int id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable("id") int id) {
        userService.deleteUserById(id);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int id,
                          @PathVariable("friendId") int friendId) {
        userService.addFriend(id, friendId);
    }


    @DeleteMapping("users/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable("id") int id,
                                  @PathVariable("friendId") int friendId) {
        userService.deleteFromFriends(id, friendId);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> getMutualFriends(@PathVariable("id") int id,
                                             @PathVariable("otherId") int otherId) {
        return userService.getMutualFriends(id, otherId);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getUsersFriends(@PathVariable("id") int id) {
        return userService.getUsersFriends(id);
    }
}


