package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.dao.LikesDbStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;
    private final LikesDbStorage likesDbStorage;
    private final DirectorStorage directorStorage;
    private final FeedStorage feedStorage;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final int LENGTH_DESCRIPTION = 200;

    @Override
    public Film addFilm(Film film) throws ValidateException {
        validateFilms(film);
        filmStorage.addFilm(film);
        if (film.getGenres() != null) {
            filmGenreStorage.removeGenreFromFilm(film.getId());
            genreStorage.setGenresToFilms(film.getId(), film.getGenres());
        }
        if (film.getDirectors() != null) {
            directorStorage.addDirectorsToFilm(film);
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidateException {
        validateFilms(film);
        filmStorage.updateFilm(film);
        filmGenreStorage.removeGenreFromFilm(film.getId());
        if (film.getGenres() != null) {
            genreStorage.setGenresToFilms(film.getId(), film.getGenres());
        }
        directorStorage.deleteDirectorsFromFilm(film);
        if (film.getDirectors() != null) {
            directorStorage.addDirectorsToFilm(film);
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();
        films.stream()
                .forEach(film -> film.getDirectors().addAll(directorStorage.getDirectorsForFilm(film.getId())));
        Map<Long, Film> filmsMap = new HashMap<>();
        for (Film film : films) {
            filmsMap.put(film.getId(), film);
        }
        return new ArrayList<>(genreStorage.getGenresForFilm(filmsMap).values());
    }

    @Override
    public Film getFilmById(long id) {
        Film film = filmStorage.getFilmById(id);
        film.getDirectors().addAll(directorStorage.getDirectorsForFilm(film.getId()));
        Map<Long, Film> filmsMap = new HashMap<>();
        filmsMap.put(film.getId(), film);
        return genreStorage.getGenresForFilm(filmsMap).get(film.getId());
    }

    @Override
    public void deleteFilm(long id) {
        filmStorage.deleteFilm(id);
    }

    @Override
    public void addLike(long filmId, long userId) {
        getFilmById(filmId);
        userStorage.getUserById(userId);
        likesDbStorage.addLike(filmId, userId);
        feedStorage.addEntityToFeed(userId, "ADD", "LIKE", filmId);
    }

    @Override
    public Film removeLike(long filmId, long userId) {
        Film film = getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм не найден");
        }
        userStorage.getUserById(userId);
        likesDbStorage.deleteLike(filmId, userId);
        feedStorage.addEntityToFeed(userId, "REMOVE", "LIKE", filmId);
        return getFilmById(filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        List<Film> popularFilms = filmStorage.getMostPopularFilms(count);
        Map<Long, Film> filmsMap = new HashMap<>();
        for (Film film : popularFilms) {
            filmsMap.put(film.getId(), film);
        }
        return new ArrayList<>(genreStorage.getGenresForFilm(filmsMap).values());
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        directorStorage.getDirectorById(directorId);
        List<Film> films = filmStorage.getFilmsByDirector(directorId, sortBy);
        films.stream()
                .forEach(film -> {
                    film.getDirectors().addAll(directorStorage.getDirectorsForFilm(film.getId()));
                    film.getGenres().addAll(genreStorage.getGenreByFilm(film.getId()));
                });
        return films;
    }

    @Override
    public List<Film> getFilmsByQuery(String query, String type) {
        List<Film> films = filmStorage.getFilmsByQuery(query, type);
        films.stream()
                .forEach(film -> {
                    film.getDirectors().addAll(directorStorage.getDirectorsForFilm(film.getId()));
                    film.getGenres().addAll(genreStorage.getGenreByFilm(film.getId()));
                });
        return films;
    }

    public void validateFilms(Film film) throws ValidateException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("ERROR: поле Name не может быть пустым");
            throw new ValidateException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > LENGTH_DESCRIPTION) {
            log.error("ERROR: описание Description не может быть длиннее 200 символов");
            throw new ValidateException("Максимальная длина описания — " + LENGTH_DESCRIPTION);
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE) || film.getReleaseDate() == null) {
            log.error("ERROR: дата релиза у фильма не может быть раньше 28 декабря 1895 года");
            throw new ValidateException("Дата релиза фильма — не раньше " + MIN_RELEASE_DATE);
        }
        if (film.getDuration() <= 0) {
            log.error("ERROR: продолжительность фильма должна быть положительной");
            throw new ValidateException("Продолжительность фильма должна быть положительной");
        }
        if (film.getMpa() == null) {
            log.error("ERROR: MPA не загрузился");
            throw new ValidateException("Необходимо добавить MPA");
        }
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    @Override
    public List<Film> getRecommendation(long userId) {
        List<Film> films = filmStorage.getRecommendation(userId);
        films = directorStorage.setDirectorsForFilms(films);
        Map<Long, Film> filmsMap = new HashMap<>();
        for (Film film : films) {
            filmsMap.put(film.getId(), film);
        }
        return new ArrayList<>(genreStorage.getGenresForFilm(filmsMap).values());
    }
}
