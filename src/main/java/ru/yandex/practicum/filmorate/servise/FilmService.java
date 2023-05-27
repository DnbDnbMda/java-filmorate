package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void userSetLikeFilm(int id, int userId) {
        Film updateFilm = filmStorage.getFilmById(id);
        if (!updateFilm.getLikes().contains((long) userId)) {
            updateFilm.getLikes().add((long) userId);
            updateFilm.setCountOfLikes(updateFilm.getCountOfLikes() + 1);
        }
    }

    public void userDeleteLikeFilm(int id, int userId) {
        Film updateFilm = filmStorage.getFilmById(id);
        if (updateFilm.getLikes().contains((long) userId)) {
            updateFilm.getLikes().remove((long) userId);
            updateFilm.setCountOfLikes(updateFilm.getCountOfLikes() - 1);
        }

    }

    public List<Film> getPopularFilms(int count) {
        List<Film> listFilms = filmStorage.getAllFilms();
        listFilms.stream().sorted(Comparator.comparing(
                Film::getCountOfLikes)).limit(count).collect(Collectors.toList());
        return listFilms;
    }
}
