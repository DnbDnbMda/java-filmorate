package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();

    @PostMapping("/users")
    public User putUser(@Valid @RequestBody User user) throws ValidEx {
        if (user.getLogin().isEmpty()) {
            throw new ValidEx("Логин не может быть пустым");
        } else if (user.getEmail().isEmpty()) {
            throw new ValidEx("Почта не может быть пустой");
        } else if (!user.getEmail().contains("@")) {
            throw new ValidEx("Почта должна содержать знак @");
        } else if (user.getLogin().contains(" ")) {
            throw new ValidEx("Логин не может содержать знаков пробела");
        } else if (user.getBirthday().after(new Date())) {
            throw new ValidEx("Дата рождения пользователя не может быть больше текущей даты");
        } else {
            users.put(user.getId(), user);
            log.info("Создан пользователь");
            return user;
        }
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
