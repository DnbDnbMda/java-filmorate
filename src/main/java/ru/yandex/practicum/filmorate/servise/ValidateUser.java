package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidEx;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

@Component
public class ValidateUser {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public ValidateUser(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public boolean validateUserData(User user) throws ValidEx {
        if (user.getId() == null) {
            user.setId(inMemoryUserStorage.generateIdUser());
        }
        if (user.getName() == null || user.getName().equals("")) {
            user.setName("common");
        }
        if (user.getLogin() == null) {
            user.setLogin("common");
        }
        if (user.getLogin().isEmpty()) {
            throw new ValidEx("Логин не может быть пустым");
        } else if (user.getEmail().isEmpty()) {
            throw new ValidEx("Почта не может быть пустой");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidEx("Почта должна содержать знак @");
        } else if (user.getLogin().contains(" ")) {
            throw new ValidEx("Логин не может содержать знаков пробела");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidEx("Дата рождения пользователя не может быть больше текущей даты");
        } else {
            return true;
        }
    }
}
