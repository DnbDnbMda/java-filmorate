package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidEx;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;

@Component
public class ValidateFilm {
    private static final LocalDate LIMIT_DATE = LocalDate.of(1895, 12, 28);
    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public ValidateFilm(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public boolean validateFilmData(Film film) throws ValidEx {
        if (film.getId() == null) {
            film.setId(inMemoryFilmStorage.generateIdFilm());
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
