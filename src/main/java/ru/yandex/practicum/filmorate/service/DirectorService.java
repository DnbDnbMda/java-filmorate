package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {

    public Director addDirector(Director director);

    public Director updateDirector(Director director);

    public List<Director> getAllDirectors();

    Director getDirectorById(int id);

    void deleteDirector(int id);
}
