package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(long id);

    List<Film> getAllFilms();

    List<Film> getMostPopularFilms(Integer count);

    List<Film> getFilmsByDirector(int directorId, String sortBy);

    void deleteFilm(long id);
}
