package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    public DirectorServiceImpl(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    @Override
    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    @Override
    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    @Override
    public Director getDirectorById(int id) {
        return directorStorage.getDirectorById(id);
    }

    @Override
    public void deleteDirector(int id) {
        directorStorage.deleteDirector(id);
    }
}
