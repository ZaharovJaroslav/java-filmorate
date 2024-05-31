package ru.yandex.practicum.filmorate.storage.film;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id;


    private int setId() {
        return ++id;
    }

    @Override
    public Film addFilm(Film film) {
        log.info("Добалвение фильма");
        int lastId = setId();
        if (film.getId() <= lastId) {
            film.setId(lastId);
            log.debug("Генерация и присвоение уникального id фильму. id = {}", id);
        } else {
            id = film.getId();
        }
        log.debug("Валидация фильма c id = {}", film.getId());
        Film newFilm = validationFilm(film);
        films.put(lastId, newFilm);
        log.info("Фильм {} успешно добавлен", newFilm.getName());
        return newFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        log.info("Обновление фильма");
        if (films.containsKey(film.getId())) {
            Film oldfilm  = films.get(film.getId());
            log.debug("Обновление фильма: id = {}; name = {}", oldfilm.getId(), oldfilm.getName());
            oldfilm = validationFilm(film);
            log.info("Фильм успешно обновлен");
            return oldfilm;
        }
        log.warn("Ошибка обновления фильма: id = {}; name = {}", film.getId(), film.getName());
        throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
    }

    public Collection<Film> getFilms() {
        log.info("Получение списка всех фильмов");
        return films.values();
    }

    @Override
    public void deleteFilmById(int id) {
        log.info("Удаление фильма");
        if (films.containsKey(id)) {
            films.remove(id);
            log.info("Фильм успешно удален");
        } else {
            log.warn("Ошибка удаления фильма: id = {}", id);
            throw new NotFoundException("Фильм для удаления с id = " + id + " не найден");
        }
    }

    public Film validationFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка валидации: название фильма - <{}>", film.getName());
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()
                || film.getDescription().length() > DESCRIPTION_LENGTH) {
            log.warn("Ошибка валидации: описание фильма - <{}>", film.getDescription());
            throw new ValidationException("Описание не может быть путстым, максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MOVIE_BIRTHDAY)) {
            log.warn("Ошибка валидации: дата релиза фильма - <{}>", film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть пустым, а так же раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Ошибка валидации: продолжительность фильма - <{}>", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительным числом " +
                    "а так же не может быть меньше 1 секунды");
        }
        return film;
    }
}
