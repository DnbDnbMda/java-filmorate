package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface DirectorStorage {

    public Director addDirector(Director director);

    public Director updateDirector(Director director);

    public List<Director> getAllDirectors();

    public Director getDirectorById(long id);

    public void deleteDirector(long id);

    List<Director> getDirectorsForFilm(long filmId);

    void addDirectorsToFilm(Film film);

    void deleteDirectorsFromFilm(Film film);
}
