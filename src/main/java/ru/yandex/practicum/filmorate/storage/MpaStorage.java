package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaStorage {
    List<MpaRating> getAllMpa();

    MpaRating getMpaById(int id);
}