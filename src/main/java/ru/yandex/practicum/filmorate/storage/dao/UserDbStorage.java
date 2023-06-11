package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Primary
@Slf4j
@Component
public class UserDbStorage implements UserStorage {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("name", user.getName());
        values.put("login", user.getLogin());
        values.put("birthday", user.getBirthday());
        Number userKey = simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource(values));
        user.setId(userKey.longValue());
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlUpdUs = "UPDATE USERS SET EMAIL = :email, LOGIN = :login, NAME = :name," +
                "                BIRTHDAY = :birthday where USER_ID = :id";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("email", user.getEmail())
                .addValue("login", user.getLogin())
                .addValue("name", user.getName())
                .addValue("birthday", user.getBirthday())
                .addValue("id", user.getId());

        int rowsAffected = namedParameterJdbcTemplate.update(sqlUpdUs, sqlParameterSource);
        if (rowsAffected == 0) {
            throw new NotFoundException("Не существует пользователя с id {}: " + user.getId());
        }
        return user;
    }

    @Override
    public User getUserById(long id) {
        String sqlUserById = "select * from users where users.user_id = ?";
        List<User> users = jdbcTemplate.query(sqlUserById, (rs, rowNum) -> mapRowToUser(rs), id);
        if (users.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %s не найден", id));
        }
        return users.get(0);
    }

    @Override
    public List<User> getAllUsers() {
        String sqlAllUsers = "select * from users";
        return namedParameterJdbcTemplate.query(sqlAllUsers, (rs, rowNum) -> mapRowToUser(rs));
    }

    public List<User> getAllById(List<Long> ids) {
        var sqlAllId = "SELECT * FROM users WHERE user_id IN (:ids)";
        var idsParams = new MapSqlParameterSource("ids", ids);
        return namedParameterJdbcTemplate.query(sqlAllId, idsParams, (rs, rowNum) -> mapRowToUser(rs));
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String name = rs.getString("name");
        String login = rs.getString("login");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        User user = User.builder()
                .id(id)
                .email(email)
                .name(name)
                .login(login)
                .birthday(birthday)
                .build();
        return user;
    }
}