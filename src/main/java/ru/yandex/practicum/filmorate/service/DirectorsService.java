package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.db.directors.DirectorDao;

import java.util.Collection;
import java.util.Optional;

@Service
public class DirectorsService {

    private final DirectorDao directorDao;

    @Autowired
    public DirectorsService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public Collection<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }

    public Optional<Director> getDirectorById(long id) {
        return directorDao.getDirectorById(id);
    }

    public Director createDirector(Director director) {
        return directorDao.create(director);
    }

    public Director updateDirector(Director director) {
        return directorDao.update(director);
    }

    public boolean deleteDirector(long id) {
        return directorDao.deleteById(id);
    }
}
