package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.UserEvent;
import ru.yandex.practicum.filmorate.service.UserEventService;

import java.util.Collection;

@RestController
@ResponseBody
@RequestMapping("/users/{id}/feed")
public class UserFeedController {
    private final UserEventService userEventService;

    @Autowired
    public UserFeedController(UserEventService userEventService) {
        this.userEventService = userEventService;
    }

    @GetMapping
    public Collection<UserEvent> getByUser(@PathVariable("id") int id) {
        return userEventService.getByUser(id);
    }
}


