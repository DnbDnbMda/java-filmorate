package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@Slf4j
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaServiceImpl(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    @Override
    public List<MpaRating> getAllMpa() {
        log.info("Получен список рейтингов MPA");
        return mpaStorage.getAllMpa();
    }

    @Override
    public MpaRating getMpaById(int id) {
        log.info("Получен рейтинг MPA с id", id);
        return mpaStorage.getMpaById(id);
    }
}
