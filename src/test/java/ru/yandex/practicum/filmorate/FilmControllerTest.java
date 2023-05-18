package ru.yandex.practicum.filmorate;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidEx;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@SpringBootTest
public class FilmControllerTest {

    @Test
    public void shouldNameOfFilmIsEmpty() {
        FilmController filmController = new FilmController();

        Film film = Film.builder()
                .name("")
                .id(1)
                .description("Описание")
                .realeseDate(Date.from(Instant.parse("1895-12-28T00:00:00.00Z")))
                .duration(Duration.ofSeconds(1))
                .build();

        assertThrows(ValidEx.class, () -> filmController.putFilm(film));
    }

    @Test
    public void shouldLengthOfDescriptionGreater200() {
        FilmController filmController = new FilmController();
        String description = "d".repeat(201);
        Film film = Film.builder()
                .name("название фильма")
                .id(1)
                .description(description)
                .realeseDate(Date.from(Instant.parse("1895-12-28T00:00:00.00Z")))
                .duration(Duration.ofSeconds(1))
                .build();

        assertThrows(ValidEx.class, () -> filmController.putFilm(film));
    }

    @Test
    public void shouldLengthOfDescriptionEquals200() throws ValidEx {
        FilmController filmController = new FilmController();
        String description = "d".repeat(200);
        Date date = Date.from(Instant.parse("1895-12-28T00:00:00.00Z"));

        Film film = Film.builder()
                .name("название фильма")
                .id(1)
                .description(description)
                .realeseDate(Date.from(Instant.parse("1895-12-28T00:00:00.00Z")))
                .duration(Duration.ofSeconds(1))
                .build();

        filmController.putFilm(film);
        assertEquals(film, filmController.getFilm().get(0));
    }

    @Test
    public void shouldDateEquals28121985() throws ValidEx {
        FilmController filmController = new FilmController();

        Film film = Film.builder()
                .name("название фильма")
                .id(1)
                .description("описание")
                .realeseDate(Date.from(Instant.parse("1895-12-28T00:00:00.00Z")))
                .duration(Duration.ofSeconds(1))
                .build();

        filmController.putFilm(film);
        assertEquals(film, filmController.getFilm().get(0));
    }

    @Test
    public void shouldDateBefore28121985() throws ValidEx {
        FilmController filmController = new FilmController();

        Film film = Film.builder()
                .name("название фильма")
                .id(1)
                .description("описание")
                .realeseDate(Date.from(Instant.parse("1895-12-27T23:59:59.99Z")))
                .duration(Duration.ofSeconds(1))
                .build();

        assertThrows(ValidEx.class, () -> filmController.putFilm(film));
    }


    @Test
    public void shouldDurationMoreZero() throws ValidEx {
        FilmController filmController = new FilmController();

        Film film = Film.builder()
                .name("название фильма")
                .id(1)
                .description("описание")
                .realeseDate(Date.from(Instant.parse("1895-12-28T00:00:00.00Z")))
                .duration(Duration.ofSeconds(0))
                .build();

        assertThrows(ValidEx.class, () -> filmController.putFilm(film));
    }
}
