package ru.yandex.practicum.filmorate.exception;

public class ValidateException extends RuntimeException {
    public ValidateException(final String message) {
        super(message);
    }
}
