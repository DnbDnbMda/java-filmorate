package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@Primary
public class DirectorDbStorage implements DirectorStorage {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public Director addDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("id")
                .usingColumns("name");
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(director);
        director.setId(simpleJdbcInsert.executeAndReturnKey(sqlParameterSource).intValue());
        log.info("Добавлен директор id = {}", director.getId());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        getDirectorById(director.getId()); //проверка наличия директора в БД, если такого айди нет, кидается искл
        String sql = "update directors set name = ?";
        jdbcTemplate.update(sql, director.getName());
        log.info("Обновлен директор id = {}", director.getId());
        return director;
    }

    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director getDirectorById(long id) {
        String sql = "SELECT * FROM directors WHERE id = ?;";
        try {
            Director director = jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> makeDirector(rs)), id);
            return director;
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("Director not found");
        }
    }

    @Override
    public void deleteDirector(long id) {
        getDirectorById(id); //проверка наличия директора в БД, если такого айди нет, кидается искл
        String sqlFilmDirector = "delete * from film_director where director_id = ?";
        jdbcTemplate.update(sqlFilmDirector, id);
        String sqlDirectors = "delete * from directors where id = ?";
        jdbcTemplate.update(sqlDirectors, id);
        log.info("Директор id = {} удален", id);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
