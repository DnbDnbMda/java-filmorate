package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFound extends Exception {
    public UserNotFound(String message) {
        super(message);
        log.error("Пользователь не найден");
    }
}
