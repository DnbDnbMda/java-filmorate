package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface FeedStorage {
    void addEntityToFeed(long userId, String operation, String eventType, long entityId);

    Collection<Event> getUserFeed(long userId);
}
