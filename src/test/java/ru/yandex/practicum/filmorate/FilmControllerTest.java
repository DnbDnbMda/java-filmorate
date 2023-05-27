package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidEx;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

@SpringBootTest
public class FilmControllerTest {

   /* @Test
    public void shouldNameOfFilmIsEmpty() {
        FilmController filmController = new FilmController(validateFilm, userStorage);

        Film film = Film.builder()
                .name("")
                .id(1)
                .description("Описание")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();

        assertThrows(ValidEx.class, () -> filmController.createFilm(film));
    }

    @Test
    public void shouldLengthOfDescriptionGreater200() {
        FilmController filmController = new FilmController(validateFilm, userStorage);
        String description = "d".repeat(201);
        Film film = Film.builder()
                .name("название фильма")
                .id(1)
                .description(description)
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();

        assertThrows(ValidEx.class, () -> filmController.createFilm(film));
    }

    @Test
    public void shouldLengthOfDescriptionEquals200() throws ValidEx {
        FilmController filmController = new FilmController(validateFilm, userStorage);
        String description = "d".repeat(200);
        Date date = Date.from(Instant.parse("1895-12-28T00:00:00.00Z"));

        Film film = Film.builder()
                .name("название фильма")
                .id(1)
                .description(description)
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();

        filmController.createFilm(film);
        assertEquals(film, filmController.getFilm().get(0));
    }

    @Test
    public void shouldDateEquals28121985() throws ValidEx {
        FilmController filmController = new FilmController(validateFilm, userStorage);

        Film film = Film.builder()
                .name("название фильма")
                .id(1)
                .description("описание")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(1)
                .build();

        filmController.createFilm(film);
        assertEquals(film, filmController.getFilm().get(0));
    }

    @Test
    public void shouldDateBefore28121985() throws ValidEx {
        FilmController filmController = new FilmController(validateFilm, userStorage);

        Film film = Film.builder()
                .name("название фильма")
                .id(1)
                .description("описание")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(1)
                .build();

        assertThrows(ValidEx.class, () -> filmController.createFilm(film));
    }


    @Test
    public void shouldDurationMoreZero() throws ValidEx {
        FilmController filmController = new FilmController(validateFilm, userStorage);

        Film film = Film.builder()
                .name("название фильма")
                .id(1)
                .description("описание")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(0)
                .build();

        assertThrows(ValidEx.class, () -> filmController.createFilm(film));
    }*/
}
