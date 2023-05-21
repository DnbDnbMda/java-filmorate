package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.ValidateUser;

import java.util.*;

@Slf4j
@RestController
public class UserController {
    private final ValidateUser validateUser = new ValidateUser();
    public static final HashMap<Integer, User> users = new HashMap<>();

    @PostMapping("/users")
    public User create(@RequestBody User user) throws ValidEx {
        if (validateUser.validateUserData(user)) {
            users.put(user.getId(), user);
            log.info("Создан пользователь: {}", user);
            return new ResponseEntity<User>(user, HttpStatus.CREATED).getBody();
        } else {
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST).getBody();
        }
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) throws ValidEx {
        if (validateUser.validateUserData(user)) {
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
}

