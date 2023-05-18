package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidEx;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private static final Date LIMITE_DATE = Date.from(Instant.parse("1895-12-28T00:00:00.00Z"));

    @PostMapping("/films")
    public Film putFilm(@Valid @RequestBody Film film) throws ValidEx {
        if (validateFilmData(film)) {
            films.put(film.getId(), film);
            log.info("Добавлен фильм");
            return film;
        } else return null;
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidEx {
        if (validateFilmData(film)) {
            films.put(film.getId(), film);
            log.info("Данные фильма обновлены");
            return film;
        } else return null;
    }

    @GetMapping("/films")
    public List<Film> getFilm() {
        log.info("Получен запрос на получение списка фильмов");
        return new ArrayList<>(films.values());
    }

    public boolean validateFilmData(Film film) throws ValidEx {
        if (film.getName().isBlank()) {
            throw new ValidEx("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidEx("Описание фильма не может превышать 200 знаков");
        } else if (film.getRealeseDate().after(LIMITE_DATE) | !film.getRealeseDate().equals(LIMITE_DATE)) {
            throw new ValidEx("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        } else if (film.getDuration().isNegative() | film.getDuration().isZero()) {
            throw new ValidEx("Длительность фильма должна быть больше нуля");
        } else {
            return true;
        }
    }


}

