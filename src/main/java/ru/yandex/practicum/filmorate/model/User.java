package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private long id;
    @NotBlank
    @Email
    private String email; // электронная почта не может быть пустой и должна содержать символ @;
    @NotBlank
    @Pattern(regexp = "\\S+") //логин не может быть пустым и содержать пробелы;
    private String login;
    private String name;
    @PastOrPresent
    private LocalDate birthday; //дата рождения не может быть в будущем.
    private final Set<Long> friends = new HashSet<>();
}
