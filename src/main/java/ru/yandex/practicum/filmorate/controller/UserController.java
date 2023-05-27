package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.ValidateUser;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Slf4j
@RestController
public class UserController {
    private final ValidateUser validateUser;
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserController(ValidateUser validateUser, InMemoryUserStorage inMemoryUserStorage) {
        this.validateUser = validateUser;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @PostMapping("/users")
    public User create(@RequestBody User user) throws ValidEx {
        if (validateUser.validateUserData(user)) {
            inMemoryUserStorage.createUser(user.getId(), user);
            log.info("Создан пользователь: {}", user);
            return new ResponseEntity<User>(user, HttpStatus.CREATED).getBody();
        } else {
            return new ResponseEntity<User>(HttpStatus.BAD_REQUEST).getBody();
        }
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) throws ValidEx {
        if (validateUser.validateUserData(user)) {
            if (!inMemoryUserStorage.containsUserById(user.getId())) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
            }
            inMemoryUserStorage.updateUser(user.getId(), user);
            log.info("Пользователь обновлен {}", user);
            return user;
        } else {
            return new ResponseEntity<User>(HttpStatus.INTERNAL_SERVER_ERROR).getBody();
        }
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return inMemoryUserStorage.getAllUsers();
    }
}

