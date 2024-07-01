package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.genre.GenreDao;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDao genreDao;

    public Genre getGenreById(int id) {
        log.debug("getGenreById({})", id);
        if (!genreDao.isContains(id)) {
            throw new NotFoundException("Был передан отрицательный или пустой идентификатор");
        }
        return genreDao.getGenreById(id);
    }

    public Collection<Genre> getGenres() {
        log.debug("getGenres");
        return genreDao.getGenres();
    }

}
