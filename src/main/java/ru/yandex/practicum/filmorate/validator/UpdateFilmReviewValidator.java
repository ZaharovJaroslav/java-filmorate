package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.request.FilmReviewRequest;

@Slf4j
@Component("UpdateFilmReviewValidator")
public class UpdateFilmReviewValidator extends CreateFilmReviewValidator {
    public UpdateFilmReviewValidator() {
        super();
    }

    public void validate(FilmReviewRequest request) {
        this.clean();
        if (request.getReviewId() == null) {
            log.error("Не указан идентификатор отзыва");
            validateResult.add("Необходимо указать идентификатор отзыва");
        }
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
