package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.storage.UserStorage;

public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

}
