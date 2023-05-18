package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Builder
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
}
