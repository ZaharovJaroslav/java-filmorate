package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpaById(Integer id) {
        log.debug("getMpaById({})",id);
        Mpa mpa = jdbcTemplate.queryForObject("SELECT mpa_id, mpa_rating FROM film_mpa WHERE mpa_id=?",
               new MpaMapper(), id);
        log.trace("Был возвращен рейтинг MPA {}", mpa);
        return mpa;

    }

    @Override
    public List<Mpa> getMpaList() {
        log.debug("getMpaList()");
        List<Mpa> mpaList = jdbcTemplate.query("SELECT mpa_id, mpa_rating FROM film_mpa ORDER BY mpa_id",
                new MpaMapper());
        log.trace("Это все данные рейтинга mpa: {}", mpaList);
        return mpaList;
    }

    @Override
    public boolean isContains(Integer id) {
        log.debug("isContains({})", id);
        try {
            getMpaById(id);
            log.trace("Был найден MPA с идентификатором {}", id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            log.trace("MPA с идентификатором {} не найден", id);
            return false;
        }
    }
}
