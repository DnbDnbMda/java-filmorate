package ru.yandex.practicum.filmorate.model;

import lombok.*;

import java.time.LocalDate;
import java.util.TreeSet;

@Data
@Setter
@Getter
@ToString
public class Film {
    private Integer id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private long rate;
    private TreeSet<Long> likes;
    private Integer countOfLikes;

    public Film(Integer id, String name, String description, LocalDate releaseDate, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = 0;
        this.likes = new TreeSet<>();
        this.countOfLikes = 0;
    }

    public Integer getCountOfLikes() {
        return countOfLikes;
    }
}
