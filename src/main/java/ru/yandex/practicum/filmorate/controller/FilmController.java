package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidEx;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.servise.FilmService;
import ru.yandex.practicum.filmorate.servise.ValidateFilm;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.*;

@Slf4j
@RestController
public class FilmController {
    private final ValidateFilm validateFilm;
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(ValidateFilm validateFilm, InMemoryFilmStorage inMemoryFilmStorage, FilmService filmService) {
        this.validateFilm = validateFilm;
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.filmService = filmService;
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

    @GetMapping("films/{id}")
    public Film getFilmById(@PathVariable String id) {
        Film filmById = inMemoryFilmStorage.getFilmById(Integer.parseInt(id));
        if (filmById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else return filmById;
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void userSetLikeFilm(@PathVariable String id, @PathVariable String userId) {
        filmService.userSetLikeFilm(Integer.parseInt(id), Integer.parseInt(userId));
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void userDeleteLikeFilm(@PathVariable String id, @PathVariable String userId) {
        filmService.userDeleteLikeFilm(Integer.parseInt(id), Integer.parseInt(userId));
    }

    @GetMapping(value = {"/films/popular?count={count}", "/films/popular"})
    @ResponseBody
    public List<Film> getPopularFilms(@RequestParam(required = false) String count) {
        int countOfPopularFilms = 0;
        if (count == null) {
            countOfPopularFilms = 10;
        } else {
            countOfPopularFilms = Integer.parseInt(count);
        }
        return filmService.getPopularFilms(countOfPopularFilms);
    }

}

