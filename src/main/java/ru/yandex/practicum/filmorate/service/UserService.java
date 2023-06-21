package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    User addUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();

    User getUserById(long id);

    void addToFriend(long friendId, long userId);

    List<User> getCommonFriends(long userId, long friendId);

    void deleteFromFriend(long friendId, long userId);

    List<User> getAllFriends(long id);

    List<User> getUsersByIds(List<Long> ids);

    void deleteUser(long id);
}
