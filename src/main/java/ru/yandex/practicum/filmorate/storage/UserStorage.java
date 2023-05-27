package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    void createUser(int userId, User user);

    void deleteUser();

    void updateUser(int userId, User user);

    boolean containsUserById(int id);

    int generateIdUser();

    List<User> getAllUsers();
}
