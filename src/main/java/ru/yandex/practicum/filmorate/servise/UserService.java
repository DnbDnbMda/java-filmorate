package ru.yandex.practicum.filmorate.servise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidEx;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public TreeSet<User> getCommonFriends(int idUser1, int idUser2) {
        Comparator<User> comparator = new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getId() - o2.getId();
            }
        };
        User user1 = userStorage.getUserById(idUser1);
        User user2 = userStorage.getUserById(idUser2);
        TreeSet<Long> commonIdUsers = new TreeSet<>(user1.getFriends());
        if (user1 == null || user2 == null) {
            return new TreeSet<User>(comparator);
        } else {
            TreeSet<Long> friendsUser1 = user1.getFriends();
            TreeSet<Long> friendsUser2 = user2.getFriends();
            if (friendsUser1.isEmpty() || friendsUser2.isEmpty()) {
                return new TreeSet<User>(comparator);
            } else {
                commonIdUsers.retainAll(friendsUser2);
                return convertListIdToListUser(commonIdUsers);
            }
        }
    }

    public void addFriend(int idUser, int idFriend) throws ValidEx {
        if (userStorage.getUserById(idUser) == null || userStorage.getUserById(idFriend) == null) {
            throw new ValidEx("Не найден пользователь");
        } else {
            User user1 = userStorage.getUserById(idUser);
            User user2 = userStorage.getUserById(idFriend);
            TreeSet<Long> friendsUser1 = user1.getFriends();
            friendsUser1.add((long) idFriend);
            TreeSet<Long> friendsUser2 = user2.getFriends();
            friendsUser2.add((long) idUser);

        }
    }

    public TreeSet<User> getFriendsOfUserById(int idUser) throws ValidEx {
        if (userStorage.getUserById(idUser) == null) {
            throw new ValidEx("Не найден пользователь");
        } else {
            User user = userStorage.getUserById(idUser);
            TreeSet<Long> listOfIdFriends;
            listOfIdFriends = user.getFriends();
            return convertListIdToListUser(listOfIdFriends);
        }
    }

    public TreeSet<User> convertListIdToListUser(TreeSet<Long> listOfIdUsers) {
        Comparator<User> comparator = new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getId() - o2.getId();
            }
        };
        TreeSet<User> listOfFriends = new TreeSet<>(comparator);
        for (Long eachId : listOfIdUsers) {
            listOfFriends.add(userStorage.getUserById(eachId.intValue()));
        }
        return listOfFriends;
    }

    public void deleteFriendById(int idUser, int idFriend) throws ValidEx {
        if (userStorage.getUserById(idUser) == null || userStorage.getUserById(idFriend) == null) {
            throw new ValidEx("Не найден пользователь");
        } else {
            User user1 = userStorage.getUserById(idUser);
            User user2 = userStorage.getUserById(idFriend);
            TreeSet<Long> friendsUser1 = user1.getFriends();
            friendsUser1.remove((long) idFriend);
            TreeSet<Long> friendsUser2 = user2.getFriends();
            friendsUser2.remove((long) idUser);
        }
    }
}
