package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.servise.UserService;
import ru.yandex.practicum.filmorate.servise.ValidateUser;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

@Slf4j
@RestController
public class UserController {
    private final ValidateUser validateUser;
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserService userService;

    @Autowired
    public UserController(ValidateUser validateUser, InMemoryUserStorage inMemoryUserStorage, UserService userService) {
        this.validateUser = validateUser;
        this.inMemoryUserStorage = inMemoryUserStorage;
        this.userService = userService;
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

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable String id) {
        User userById = inMemoryUserStorage.getUserById(Integer.parseInt(id));
        if (userById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return userById;
    }

    //Добавление в друзья
    @PutMapping(value = "/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable String id, @PathVariable String friendId) {
        try {
            userService.addFriend(Integer.parseInt(id), Integer.parseInt(friendId));
        } catch (ValidEx e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value = "/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable String id, @PathVariable String friendId) throws ValidEx {
        userService.deleteFriendById(Integer.parseInt(id), Integer.parseInt(friendId));
    }

    @GetMapping(value = "/users/{id}/friends/common/{otherId}")
    public TreeSet<User> getCommonFriends(@PathVariable String id, @PathVariable String otherId) {
        return userService.getCommonFriends(Integer.parseInt(id), Integer.parseInt(otherId));
    }

    @GetMapping(value = "/users/{id}/friends")
    public TreeSet<User> getFriendsOfUserById(@PathVariable String id) throws ValidEx {
        int userId = Integer.parseInt(id);
        return userService.getFriendsOfUserById(userId);
    }
}

