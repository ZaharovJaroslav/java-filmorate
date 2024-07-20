package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.request.FilmReviewRequest;
import ru.yandex.practicum.filmorate.storage.db.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.db.filmReview.FilmReviewStorage;
import ru.yandex.practicum.filmorate.storage.db.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.db.user.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class FilmReviewService {
    private final FilmReviewStorage filmReviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmReviewService(@Qualifier("FilmReviewDbStorage") FilmReviewStorage filmReviewStorage,
                             @Qualifier("FilmDbStorage") FilmStorage filmStorage,
                             @Qualifier("UserDbStorage") UserDbStorage userStorage) {
        this.filmReviewStorage = filmReviewStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<FilmReview> getFilmReviews(Optional<Long> filmId, int count) {
        return filmId.isEmpty()
                ? filmReviewStorage.getFilmReviews(count)
                : filmReviewStorage.getFilmReviewsByFilm(filmId.get(), count);
    }

    public FilmReview find(long id) {
        Optional<FilmReview> filmReviewOptional = filmReviewStorage.find(id);
        if (filmReviewOptional.isEmpty()) {
            throw new NotFoundException("Оценка для фильма не найдена");
        }
        return filmReviewOptional.get();
    }

    public FilmReview create(FilmReviewRequest request) {
        filmStorage.getFilmById(request.getFilmId().intValue());
        Optional<User> userOptional = userStorage.getUserById(request.getUserId().intValue());
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Optional<FilmReview> filmReviewOptional = filmReviewStorage.create(request.toFilmReview());
        if (filmReviewOptional.isEmpty()) {
            throw new NotFoundException("Оценка для фильма не найдена");
        }
        return filmReviewOptional.get();
    }

    public FilmReview update(FilmReviewRequest request) {
        filmStorage.getFilmById(request.getFilmId().intValue());
        Optional<User> userOptional = userStorage.getUserById(request.getUserId().intValue());
        if (userOptional.isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        Optional<FilmReview> filmReviewOptional = filmReviewStorage.update(request.toFilmReview());
        if (filmReviewOptional.isEmpty()) {
            throw new NotFoundException("Оценка для фильма не найдена");
        }
        return filmReviewOptional.get();
    }

    public void remove(long id) {
        filmReviewStorage.remove(id);
    }
}
