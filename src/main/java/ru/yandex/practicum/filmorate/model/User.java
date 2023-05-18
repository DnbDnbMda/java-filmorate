package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.controller.UserController;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@ToString
@EqualsAndHashCode
@Builder
@Getter
@Setter
public class User {
    @NotNull
    private final int id;
    @NotNull
    @NotBlank
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private final String email;
    @NotNull
    @NotBlank
    private final String login;
    @NotNull
    @NotBlank
    private final String name;
    @NotNull
    private final Date birthday;

    public User(int id, String email, String login, String name, Date birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(String email, String login, String name, Date birthday) {
        this.id = generateIdUser();
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    private int generateIdUser() {
        int maxId = UserController.getMaxIdUser()+1;
        return maxId;
    }
}
