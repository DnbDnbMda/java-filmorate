package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@ToString
@EqualsAndHashCode
@Builder
@Getter
@Setter
@AllArgsConstructor
public class User {
    private Integer id;
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
