package ru.yandex.practicum.filmorate.storage;

public interface FilmGenreStorage {

    void addGenreToFilm(long filmId, long genreId);

    void removeGenreFromFilm(long filmId);
}
