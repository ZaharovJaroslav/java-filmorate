package ru.yandex.practicum.filmorate.validator;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.request.FilmReviewRequest;

@Slf4j
public class UpdateFilmReviewValidator extends CreateFilmReviewValidator {
    public UpdateFilmReviewValidator(FilmReviewRequest request) {
        super(request);
    }

    public void validate() {
        if (request.getReviewId() == null) {
            log.error("Не указан идентификатор отзыва");
            validateResult.add("Необходимо указать идентификатор отзыва");
        }
        super.validate();
    }
}
