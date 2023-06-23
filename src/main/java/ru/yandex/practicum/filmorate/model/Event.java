package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Event {
    private long eventId;
    @NotNull
    private long userId;
    @NotNull
    private long entityId;
    @NotBlank
    private String eventType;
    @NotBlank
    private String operation;
    private long timestamp;
}