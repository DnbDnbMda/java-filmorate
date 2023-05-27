package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    @Builder.Default
    private long rate = 0;
    private Set<Long> likes;
    @Builder.Default
    private Integer countOfLikes = 0;
}
