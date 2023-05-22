package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidEx extends Exception {
    public ValidEx(String message) {
        super(message);
        log.error(message);
    }
}
