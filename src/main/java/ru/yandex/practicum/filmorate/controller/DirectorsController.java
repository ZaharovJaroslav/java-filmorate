package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorsService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/directors")
public class DirectorsController {
    private final DirectorsService directorsService;

    @GetMapping
    public Collection<Director> getDirectors() {
        return directorsService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorsById(@PathVariable long id) {
        log.info("Поиск режисера по id: {}", id);
        return directorsService.getDirectorById(id);
    }

    @PostMapping
    public Director createDirectors(@RequestBody Director directors) {
        log.info("Добавление режисера {}", directors);
        return directorsService.createDirector(directors);
    }

    @PutMapping
    public Director updateDirectors(@RequestBody Director directors) {
        log.info("Обновление режисера {}", directors);
        return directorsService.updateDirector(directors);
    }

    @DeleteMapping("/{id}")
    public boolean deleteDirectors(@PathVariable long id) {
        log.info("Удаление режисера {}", id);
        return directorsService.deleteDirector(id);
    }
}
