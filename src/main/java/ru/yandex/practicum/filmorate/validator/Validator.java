package ru.yandex.practicum.filmorate.validator;

import java.util.ArrayList;

/**
 * Абстрактный класс для валидации
 */
public class Validator {
    protected ru.yandex.practicum.filmorate.model.Validator validateResult;

    public Validator() {
        this.validateResult = new ru.yandex.practicum.filmorate.model.Validator();
    }

    public ArrayList<String> getMessages() {
        return validateResult.getMessages();
    }

    public boolean isValid() {
        return validateResult.isValid();
    }

    protected void clean() {
        this.validateResult = new ru.yandex.practicum.filmorate.model.Validator();
    }
}
