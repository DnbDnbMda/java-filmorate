package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;

@Component
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addEntityToFeed(long userId, String operation, String eventType, long entityId) {
        String sqlAddEntityToFeed =
                "INSERT into USERS_FEED (time_stamp, user_id, operation, event_type, entity_id) values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sqlAddEntityToFeed, Instant.now().getEpochSecond() * 1000,
                userId, operation, eventType, entityId);
    }

    @Override
    public Collection<Event> getUserFeed(long userId) {
        String sqlGetUserFeed = "SELECT * FROM users_feed WHERE user_id = ?";

        return jdbcTemplate.query(sqlGetUserFeed, (rs, rowNum) -> mapRowToEvent(rs), userId);
    }

    private Event mapRowToEvent(ResultSet rs) throws SQLException {
        long eventId = rs.getLong("event_id");
        long userId = rs.getLong("user_id");
        long entityId = rs.getLong("entity_id");
        String eventType = rs.getString("event_type");
        String operation = rs.getString("operation");
        long timestamp = rs.getLong("time_stamp");

        return Event.builder()
                .eventId(eventId)
                .userId(userId)
                .entityId(entityId)
                .eventType(eventType)
                .operation(operation)
                .timestamp(timestamp)
                .build();
    }

}
