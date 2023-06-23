package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import javax.validation.ValidationException;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Component
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(long filmId, long userId) {
        try {
            String sqlAddLike = "insert into likes (film_id, user_id) values (?, ?)";
            jdbcTemplate.update(sqlAddLike, filmId, userId);
        } catch (DataAccessException exception) {
            log.error("Пользователь c id = {} уже лайкнул фильм с id = {}", userId, filmId);
            throw new ValidationException(format("Пользователь с id = %s уже лайкнул фильм с id = %s",
                    userId, filmId));
        }
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
