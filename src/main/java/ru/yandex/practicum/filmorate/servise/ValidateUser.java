package ru.yandex.practicum.filmorate.servise;

import ru.yandex.practicum.filmorate.exception.ValidEx;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;

import static ru.yandex.practicum.filmorate.controller.UserController.users;

public class ValidateUser {

    public Integer getNewIdUser() {
        Set<Integer> listOfId = users.keySet();
        Optional<Integer> max = listOfId.stream().max(Comparator.comparing(ft -> ft));
        Integer maxId = max.orElse(0);
        return ++maxId;
    }

    public boolean validateUserData(User user) throws ValidEx {
        if (user.getId() == null) {
            user.setId(getNewIdUser());
        }
        if (user.getName() == null) {
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
