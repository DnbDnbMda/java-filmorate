package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    public static final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public void createFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(int id, Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public boolean containsFilmById(int id) {
        return films.containsKey(id);
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        if (films.containsKey(id)) {
            return films.getOrDefault(id, null);
        } else return null;
    }

    @Override
    public int generateIdFilm() {
        Set<Integer> listOfId = films.keySet();
        Optional<Integer> max = listOfId.stream().max(Comparator.comparing(ft -> ft));
        int maxId = max.orElse(0);
        return ++maxId;
    }
}
