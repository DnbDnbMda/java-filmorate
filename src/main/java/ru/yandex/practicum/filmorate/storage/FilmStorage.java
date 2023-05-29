package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    void createFilm(Film film);

    void deleteFilm();

    void updateFilm(int id, Film film);

    boolean containsFilmById(int id);

    List<Film> getAllFilms();

    Film getFilmById(int id);

    int generateIdFilm();
}
