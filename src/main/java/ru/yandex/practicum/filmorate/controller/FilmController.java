package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidEx;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.time.LocalDate;

@Slf4j
@RestController
public class FilmController {
    private static final HashMap<Integer, Film> films = new HashMap<>();
    private static final LocalDate LIMITE_DATE = LocalDate.of(1895, 12, 28);

    private int generateIdFilm() {
        int maxId = FilmController.getMaxIdFilm();
        return ++maxId;
    }

    public static Integer getMaxIdFilm() {
        Set<Integer> listOfId = films.keySet();
        Optional<Integer> max = listOfId.stream().max(Comparator.comparing(ft -> ft));
        return max.orElse(0);
    }

    @PostMapping("/films")
    public Film createFilm(@RequestBody Film film) throws ValidEx {
        if (film.getId() == null) {
            film.setId(generateIdFilm());
        }
        if (validateFilmData(film)) {
            films.put(film.getId(), film);
            log.info("Добавлен фильм");
            return new ResponseEntity<Film>(film, HttpStatus.CREATED).getBody();
        } else throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) throws ValidEx {
        if (validateFilmData(film)) {
            if (!films.containsKey(film.getId())) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
            }
            films.put(film.getId(), film);
            log.info("Данные фильма обновлены");
            return new ResponseEntity<Film>(film, HttpStatus.CREATED).getBody();
        } else throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
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
            throw new ValidEx("Описание фильма не может превышать 200 знаков"); //| !film.getReleaseDate().isEqual(LIMITE_DATE)
        } else if (film.getReleaseDate().isBefore(LIMITE_DATE)) {
            throw new ValidEx("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            throw new ValidEx("Длительность фильма должна быть больше нуля");
        } else {
            return true;
        }
    }
}

