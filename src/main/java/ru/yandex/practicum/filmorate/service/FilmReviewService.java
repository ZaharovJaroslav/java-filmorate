
package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmReview;
import ru.yandex.practicum.filmorate.request.FilmReviewRequest;
import ru.yandex.practicum.filmorate.storage.db.filmReview.FilmReviewStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class FilmReviewService {
    private final FilmReviewStorage filmReviewStorage;

    @Autowired
    public FilmReviewService(@Qualifier("FilmReviewDbStorage") FilmReviewStorage filmReviewStorage) {
        this.filmReviewStorage = filmReviewStorage;
    }

    public Collection<FilmReview> getFilmReviews() {
        return filmReviewStorage.getFilmReviews();
    }

    public FilmReview find(long id) {
        Optional<FilmReview> filmReviewOptional = filmReviewStorage.find(id);
        if (filmReviewOptional.isEmpty()) {
            throw new NotFoundException("Оценка для фильма не найдена");
        }
        return filmReviewOptional.get();
    }

    public FilmReview create(FilmReviewRequest request) {
        Optional<FilmReview> filmReviewOptional = filmReviewStorage.create(request.toFilmReview());
        if (filmReviewOptional.isEmpty()) {
            throw new NotFoundException("Оценка для фильма не найдена");
        }
        return filmReviewOptional.get();
    }

    public FilmReview update(FilmReviewRequest request) {
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
