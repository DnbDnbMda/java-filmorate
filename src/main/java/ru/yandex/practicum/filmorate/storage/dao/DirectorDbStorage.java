package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Primary
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
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
            return jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> makeDirector(rs)), id);
        } catch (EmptyResultDataAccessException exception) {
            throw new NotFoundException("Director not found");
        }
    }

    @Override
    public void deleteDirector(long id) {
        getDirectorById(id); //проверка наличия директора в БД, если такого айди нет, кидается искл
        String sqlFilmDirector = "delete from film_director where director_id = ?";
        jdbcTemplate.update(sqlFilmDirector, id);
        String sqlDirectors = "delete from directors where id = ?";
        jdbcTemplate.update(sqlDirectors, id);
        log.info("Директор id = {} удален", id);
    }

    @Override
    public List<Director> getDirectorsForFilm(long filmId) {
        String sqlDirectors = "select d.* from directors as d where d.id in " +
                "(select director_id from film_director where film_id = ?) group by d.id";
        return jdbcTemplate.query(sqlDirectors, (resultSet, rowNum) -> makeDirector(resultSet), filmId);
    }

    @Override
    public void addDirectorsToFilm(Film film) {
        String sql = "insert into film_director(director_id, film_id) values(?, ?)";
        film.getDirectors().stream()
                .filter(director -> getDirectorById(director.getId()) != null) //проверка существования режиссера в БД
                .forEach(director -> jdbcTemplate.update(sql, director.getId(), film.getId()));
    }

    @Override
    public void deleteDirectorsFromFilm(Film film) {
        String sqlDelete = "delete from film_director where film_id = ?;";
        jdbcTemplate.update(sqlDelete, film.getId());
    }

    @Override
    public List<Film> setDirectorsForFilms(List<Film> films) {
        Map<Long, Film> filmMap = new LinkedHashMap<>();
        films.forEach(film -> filmMap.put(film.getId(), film));
        Set<Long> filmIdSet = filmMap.keySet();

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("filmIdSet", filmIdSet);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sql = "SELECT d.id, d.name, fd.film_id FROM directors AS d " +
                "JOIN film_director AS fd ON d.id = fd.director_id WHERE fd.film_id IN (:filmIdSet)";
        namedParameterJdbcTemplate.query(sql, sqlParameterSource, (rs, rowNum) -> {
            Film film = filmMap.get(rs.getLong("film_id"));
            return film.getDirectors().add(makeDirector(rs));
        });
        return new ArrayList<>(filmMap.values());
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
