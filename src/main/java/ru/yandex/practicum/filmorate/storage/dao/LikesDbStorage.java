package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.List;

@Slf4j
@Component
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sqlAddLike = "MERGE INTO likes AS target " +
                "USING (SELECT CONVERT(?, INT) AS film_id, CONVERT(?, INT) AS user_id) AS source_table " +
                "ON (target.film_id = source_table.film_id AND target.user_id = source_table.user_id) " +
                "WHEN NOT MATCHED " +
                "THEN INSERT (film_id, user_id) " +
                "VALUES (source_table.film_id, source_table.user_id);";
        jdbcTemplate.update(sqlAddLike, filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String sqlDeleteLike = "delete from likes where (film_id = ? and user_id = ?)";
        jdbcTemplate.update(sqlDeleteLike, filmId, userId);
    }

    @Override
    public List<Long> getLikesByFilm(long filmId) {
        String sqlLikes = "select user_id from likes where film_id =?";
        return jdbcTemplate.query(sqlLikes, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }
}
