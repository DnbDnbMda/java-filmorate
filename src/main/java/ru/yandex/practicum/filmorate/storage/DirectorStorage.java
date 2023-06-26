package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {

    Director addDirector(Director director);

    Director updateDirector(Director director);

    List<Director> getAllDirectors();

    Director getDirectorById(long id);

    void deleteDirector(long id);

    List<Director> getDirectorsForFilm(long filmId);

    void addDirectorsToFilm(Film film);

    void deleteDirectorsFromFilm(Film film);

    List<Film> setDirectorsForFilms(List<Film> films);
}
