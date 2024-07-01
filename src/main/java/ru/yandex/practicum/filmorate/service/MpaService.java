package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDao;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDao mpaDao;

    public Mpa getMpaById(Integer id) {
        log.debug("getGenreById({})", id);
        if (id == null || !mpaDao.isContains(id)) {
            throw new NotFoundException("Был передан отрицательный или пустой идентификатор");
        }
        return mpaDao.getMpaById(id);
    }

    public Collection<Mpa> getMpaList() {
        log.debug("getMpaList");
        return mpaDao.getMpaList();
    }
}