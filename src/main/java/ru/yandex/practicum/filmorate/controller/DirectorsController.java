package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ErrorResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorsService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/directors")
public class DirectorsController {
    private final DirectorsService directorsService;

    @GetMapping
    public ResponseEntity<Collection<Director>> getDirectors() {
        Collection<Director> directors = directorsService.getAllDirectors();
        return ResponseEntity.ok(directors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDirectorsById(@PathVariable long id) {
        log.info("Поиск режиссера по id: {}", id);
        Optional<Director> director = directorsService.getDirectorById(id);
        if (director.isPresent()) {
            return ResponseEntity.ok(director.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Не найдено", "Режиссер не найден"));
        }
    }

    @PostMapping
    public ResponseEntity<Director> createDirectors(@RequestBody Director director) {
        log.info("Добавление режиссера {}", director);
        Director createdDirector = directorsService.createDirector(director);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDirector);
    }

    @PutMapping
    public ResponseEntity<?> updateDirectors(@RequestBody Director director) {
        log.info("Обновление режиссера {}", director);
        try {
            Director updatedDirector = directorsService.updateDirector(director);
            return ResponseEntity.ok(updatedDirector);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Не найдено", "Режиссер не найден"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDirectors(@PathVariable long id) {
        log.info("Удаление режиссера {}", id);
        if (!directorsService.deleteDirector(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }
}
