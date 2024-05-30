package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    String title;
    String  description;

    public ErrorResponse(String title, String  description) {
        this.title = title;
        this.description = description;
    }


}
