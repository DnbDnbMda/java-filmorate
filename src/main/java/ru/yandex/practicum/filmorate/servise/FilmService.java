package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFound;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void userSetLikeFilm(int id, int userId) throws UserNotFound {
        Film updateFilm = filmStorage.getFilmById(id);
        if (userStorage.containsUserById(userId)) {
            if (!updateFilm.getLikes().contains((long) userId)) {
                updateFilm.getLikes().add((long) userId);
                updateFilm.setCountOfLikes(updateFilm.getCountOfLikes() + 1);
            }
        } else throw new UserNotFound("Пользователь не найден");
    }

    public void userDeleteLikeFilm(int id, int userId) throws UserNotFound {
        Film updateFilm = filmStorage.getFilmById(id);
        if (userStorage.containsUserById(userId)) {
            if (updateFilm.getLikes().contains((long) userId)) {
                updateFilm.getLikes().remove((long) userId);
                updateFilm.setCountOfLikes(updateFilm.getCountOfLikes() - 1);
            }
        } else throw new UserNotFound("Пользователь не найден");
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> listFilms = filmStorage.getAllFilms();
        List<Film> sorted = listFilms.stream().
                sorted((o1, o2) -> o2.getCountOfLikes() - o1.getCountOfLikes()).limit(count).
                collect(Collectors.toList());
        return sorted;
    }
}


