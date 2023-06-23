package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{friendId}/friends/{id}") //добавление в друзья
    public void addToFriend(@PathVariable long friendId, @PathVariable long id) {
        userService.addToFriend(friendId, id);
    }

    @DeleteMapping("/{friendId}/friends/{id}") //удаление из друзей
    public void deleteFromFriend(@PathVariable long friendId, @PathVariable long id) {
        userService.deleteFromFriend(friendId, id);
    }

    @GetMapping("/{id}/friends") //возвращаем список пользователей-друзей
    public List<User> getAllFriends(@PathVariable long id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}") //получаем список общих друзей
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("{id}/recommendations") //возвращаем список фильмов-рекомендаций для пользователя
    public List<Film> getRecommendation(@PathVariable long id) {
        return userService.getRecommendation(id);
    }

    @GetMapping("/{id}/feed")
    public Collection<Event> getUserFeed(@PathVariable long id) {
        return userService.getUserFeed(id);
    }
}
