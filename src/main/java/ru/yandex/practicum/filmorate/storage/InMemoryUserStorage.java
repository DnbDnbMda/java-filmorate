package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public void createUser(int userId, User user) {
        users.put(userId, user);
    }

    @Override
    public void deleteUser() {

    }

    @Override
    public void updateUser(int userId, User user) {
        users.put(userId, user);
    }

    @Override
    public boolean containsUserById(int id) {
        return users.containsKey(id);
    }

    @Override
    public int generateIdUser() {
        Set<Integer> listOfId = users.keySet();
        Optional<Integer> max = listOfId.stream().max(Comparator.comparing(ft -> ft));
        Integer maxId = max.orElse(0);
        return ++maxId;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        return users.getOrDefault(id, null);
    }
}
