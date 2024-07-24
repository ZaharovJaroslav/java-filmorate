package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.request.FilmReviewRequest;


@Slf4j
public class CreateFilmReviewValidator extends AbstractValidator {
    protected final FilmReviewRequest request;

    public CreateFilmReviewValidator(FilmReviewRequest request) {
        this.request = request;
    }

    public void validate() {
        if (request.getFilmId() == null) {
            log.error("Не указан идентификатор фильма");
            validateResult.add("Не указан идентификатор фильма");
        }
        if (request.getUserId() == null) {
            log.error("Не указан идентификатор пользователя");
            validateResult.add("Не указан идентификатор пользователя");
        }
        if (request.getContent() == null || request.getContent().isBlank()) {
            log.error("Отзыв должен содержать текст");
            validateResult.add("Отзыв должен содержать текст");
        }
        if (request.getIsPositive() == null) {
            log.error("Не указана оценка");
            validateResult.add("Не указана оценка");
        }
    }
}
