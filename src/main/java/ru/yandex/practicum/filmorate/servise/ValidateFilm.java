package ru.yandex.practicum.filmorate.servise;

import ru.yandex.practicum.filmorate.exception.ValidEx;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

import static ru.yandex.practicum.filmorate.controller.FilmController.films;

public class ValidateFilm {
    private static final LocalDate LIMIT_DATE = LocalDate.of(1895, 12, 28);

    private int generateIdFilm() {
        Set<Integer> listOfId = films.keySet();
        Optional<Integer> max = listOfId.stream().max(Comparator.comparing(ft -> ft));
        int maxId = max.orElse(0);
        return ++maxId;
    }

    public boolean validateFilmData(Film film) throws ValidEx {
        if (film.getId() == null) {
            film.setId(generateIdFilm());
        }
        if (film.getName().isBlank()) {
            throw new ValidEx("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidEx("Описание фильма не может превышать 200 знаков");
        } else if (film.getReleaseDate().isBefore(LIMIT_DATE)) {
            throw new ValidEx("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            throw new ValidEx("Длительность фильма должна быть больше нуля");
        } else {
            return true;
        }
    }
}
