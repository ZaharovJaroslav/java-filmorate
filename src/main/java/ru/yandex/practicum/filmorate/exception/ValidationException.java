package ru.yandex.practicum.filmorate.exception;

import java.util.ArrayList;

public class ValidationException extends RuntimeException {
    private final ArrayList<String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
    }

    public ValidationException(String message, ArrayList<String> errors) {
        super(message);
        this.errors = errors;
    }
}
