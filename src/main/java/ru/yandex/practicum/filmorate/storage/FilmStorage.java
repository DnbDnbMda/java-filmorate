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

    List<Film> getFilmsByQuery(String query, String type);

    void deleteFilm(long id);

    List<Film> getCommonFilms(long userId, long friendId);

    List<Film> getRecommendation(long userId);
}
