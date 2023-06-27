package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Component
public interface GenreStorage {

    List<Genre> getAllGenres();

    Genre getGenreById(int id);

    List<Genre> getGenreByFilm(long id);

    List<Film> getGenresForFilm(List<Film> films);

    void setGenresToFilms(long filmId, List<Genre> genres);
}
