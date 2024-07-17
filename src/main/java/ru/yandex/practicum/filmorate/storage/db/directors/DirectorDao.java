package ru.yandex.practicum.filmorate.storage.db.directors;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorDao {

    Collection<Director> getAllDirectors();

    Optional<Director> getDirectorById(long id);

    Director create(Director director);

    Director update(Director director);

    boolean deleteById(long id);

    boolean existsById(long id);
}
