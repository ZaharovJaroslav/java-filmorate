package ru.yandex.practicum.filmorate.storage.db.directors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorMapper;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectorDaoImpl implements DirectorDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Director> getAllDirectors() {
        log.debug("getAllDirectors()");
        String sql = "SELECT id, name FROM directors ORDER BY id ASC";
        return jdbcTemplate.query(sql, new DirectorMapper());
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        log.debug("getDirectorById({})", id);
        String sql = "SELECT id, name FROM directors WHERE id = ?";
        try {
            Director director = jdbcTemplate.queryForObject(sql, new DirectorMapper(), id);
            return Optional.ofNullable(director);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Director create(Director director) {
        log.debug("save({})", director);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO directors (name) VALUES (?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        director.setId(keyHolder.getKey().longValue());

        return director;
    }

    @Override
    public Director update(Director director) {
        log.debug("update({})", director);
        if (!existsById(director.getId())) {
            throw new NotFoundException("Режисер с id = " + director.getId() + " не найден.");
        }
        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public boolean deleteById(long id) {
        log.debug("deleteById({})", id);
        jdbcTemplate.update("DELETE FROM film_directors WHERE director_id = ?", id);
        String sql = "DELETE FROM directors WHERE id = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public boolean existsById(long id) {
        String sql = "SELECT EXISTS (SELECT 1 FROM directors WHERE id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
    }

    @Override
    public List<Director> getDirectorsByFilmId(long filmId) {
        log.debug("getDirectorsByFilmId({})", filmId);
        String sql = "SELECT d.id, d.name FROM directors d JOIN film_directors fd ON fd.director_id = d.id WHERE fd.film_id = ?";
        return jdbcTemplate.query(sql, new DirectorMapper(), filmId);
    }
}
