package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Date;

@Data
@Builder
public class Film {
    @Valid
    @NotNull
    private final int id;
    @Valid
    @NotNull
    @NotBlank
    @NotEmpty
    private final String name;
    @Valid
    @NotNull
    @NotBlank
    @NotEmpty
    private final String description;
    @Valid
    @NotNull
    private final Date realeseDate;
    @Valid
    @NotNull
    private final Duration duration;
}
