package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import java.util.*;

@Slf4j
@RestController
public class UserController {
    private static final HashMap<Integer, User> users = new HashMap<>();

    private int generateIdUser() {
        int maxId = UserController.getMaxIdUser();
        return ++maxId;
    }

    @PostMapping("/users")
    public User create(@RequestBody User user) throws ValidEx {
        if (user.getId() == null) {
            user.setId(generateIdUser());
        }
        if (user.getName() == null) {
            user.setName("common");
        }
        if (user.getLogin() == null) {
            user.setLogin("common");
        }
        if (validateUserData(user)) {
            users.put(user.getId(), user);
            log.info("Создан пользователь: {}", user);
            return new ResponseEntity<User>(user, HttpStatus.CREATED).getBody();
        } else {
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST).getBody();
        }
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) throws ValidEx {
        if (validateUserData(user)) {
            if (!users.containsKey(user.getId())) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
            }
            users.put(user.getId(), user);
            log.info("Пользователь обновлен {}", user);
            return user;
        } else {
            return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR).getBody();
        }
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public boolean validateUserData(User user) throws ValidEx {
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

    public static Integer getMaxIdUser() {
        Set<Integer> listOfId = users.keySet();
        Optional<Integer> max = listOfId.stream().max(Comparator.comparing(ft -> ft));
        return max.orElse(0);
    }
}
