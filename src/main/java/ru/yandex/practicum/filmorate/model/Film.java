package ru.yandex.practicum.filmorate.model;

import jdk.jshell.Snippet;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class  Film {
    private long id;
    @NotBlank
    private String name; // название не может быть пустым
    @NotBlank
    @Size(max = 200) //описание должно быть не больше 200 символов
    private String description;
    private LocalDate releaseDate;
    @Positive
    private int duration; //продолжительность фильма должна быть положительной
    @NotNull
    private MpaRating mpa;
    private final Set<Long> likes = new HashSet<>();
    private final List<Genre> genres = new ArrayList<>();
}
