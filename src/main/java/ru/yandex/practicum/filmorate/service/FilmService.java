package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Film addFilm(Film film) throws ValidateException;

    Film updateFilm(Film film) throws ValidateException;

    List<Film> getAllFilms();

    Film getFilmById(long id);

    void deleteFilm(long id);

    void addLike(long filmId, long userId);

    Film removeLike(long filmId, long userId);

    List<Film> getMostPopularFilms(int count);

    List<Film> getFilmsByDirector(int directorId, String sortBy);

    List<Film> getFilmsByQuery(String query, String type);
  
    Collection<Film> getCommonFilms(long userId, long friendId);

}

