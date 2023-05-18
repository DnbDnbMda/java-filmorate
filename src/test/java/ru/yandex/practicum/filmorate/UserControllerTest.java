package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {

    @Test
    public void shouldPutUserLoginIsEmpty() {
        UserController userController = new UserController();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date birthday;

        try {
            birthday = formatter.parse("19-04-1985");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        User user = User.builder()
                .id(1)
                .login("")
                .email("test@yandex.ru")
                .name("ИвановИИ")
                .birthday(birthday)
                .build();

        assertThrows(ValidEx.class, () -> userController.putUser(user));
    }

    @Test
    public void shouldLoginContainsSpace() {
        UserController userController = new UserController();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date birthday;

        try {
            birthday = formatter.parse("19-04-1985");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        User user = User.builder()
                .id(1)
                .login("Иванов Иван")
                .email("test@yandex.ru")
                .name("ИвановИИ")
                .birthday(birthday)
                .build();

        assertThrows(ValidEx.class, () -> userController.putUser(user));
    }

    @Test
    public void shouldEmailIsEmpty() {
        UserController userController = new UserController();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date birthday;

        try {
            birthday = formatter.parse("19-04-1985");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        User user = User.builder()
                .id(1)
                .login("ИвановИван")
                .email("")
                .name("ИвановИИ")
                .birthday(birthday)
                .build();

        assertThrows(ValidEx.class, () -> userController.putUser(user));
    }

    @Test
    public void shouldEmailDoesNotContainDog() {
        UserController userController = new UserController();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date birthday;

        try {
            birthday = formatter.parse("19-04-1985");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        User user = User.builder()
                .id(1)
                .login("ИвановИван")
                .email("testyandex.ru")
                .name("ИвановИИ")
                .birthday(birthday)
                .build();

        assertThrows(ValidEx.class, () -> userController.putUser(user));
    }

    @Test
    public void shouldDateBirthIsLessThanCurrentDate() throws ValidEx {
        UserController userController = new UserController();
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date birthday;

        try {
            birthday = formatter.parse("19-04-1985");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        User user = User.builder()
                .id(1)
                .login("ИвановИван")
                .email("test@yandex.ru")
                .name("ИвановИИ")
                .birthday(birthday)
                .build();

        userController.putUser(user);
        assertEquals(user, userController.getUsers().get(0));
    }

    @Test
    public void shouldDateBirthIsEqualsCurrentDate() throws
            ValidEx {
        UserController userController = new UserController();
        Date birthday = new Date();

        User user = User.builder()
                .id(1)
                .login("ИвановИван")
                .email("test@yandex.ru")
                .name("ИвановИИ")
                .birthday(birthday)
                .build();

        userController.putUser(user);
        assertEquals(user, userController.getUsers().get(0));
    }

    @Test
    public void shouldDateBirthIsGreaterThanCurrentDate() {
        UserController userController = new UserController();
        Instant instant = new Date().toInstant().plus(Duration.ofDays(1));
        Date birthday = Date.from(instant);

        User user = User.builder()
                .id(1)
                .login("ИвановИван")
                .email("test@yandex.ru")
                .name("ИвановИИ")
                .birthday(birthday)
                .build();

        assertThrows(ValidEx.class, () -> userController.putUser(user));
    }
}
