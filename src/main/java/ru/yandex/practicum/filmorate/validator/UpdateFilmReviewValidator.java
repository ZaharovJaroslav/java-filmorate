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
        if (request.getReviewId() == null) {
            log.error("Не указан идентификатор отзыва");
            validateResult.add("Необходимо указать идентификатор отзыва");
        }
        super.validate(request);
    }
}
