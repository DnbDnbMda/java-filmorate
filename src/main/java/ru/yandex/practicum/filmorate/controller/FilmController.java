package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidEx;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.ValidateFilm;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;

@Slf4j
@RestController
public class FilmController {
    private final ValidateFilm validateFilm;
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmController(ValidateFilm validateFilm, InMemoryFilmStorage inMemoryFilmStorage) {
        this.validateFilm = validateFilm;
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody Film film) throws ValidEx {

        if (validateFilm.validateFilmData(film)) {
            inMemoryFilmStorage.createFilm(film);
            log.info("Добавлен фильм");
            return new ResponseEntity<Film>(film, HttpStatus.CREATED).getBody();
        } else throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) throws ValidEx {
        if (validateFilm.validateFilmData(film)) {
            if (!inMemoryFilmStorage.containsFilmById(film.getId())) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
            }
            inMemoryFilmStorage.updateFilm(film.getId(), film);
            log.info("Данные фильма обновлены");
            return new ResponseEntity<Film>(film, HttpStatus.CREATED).getBody();
        } else throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
    }

    @GetMapping("/films")
    public List<Film> getFilm() {
        log.info("Получен запрос на получение списка фильмов");
        return inMemoryFilmStorage.getAllFilms();
    }
}

