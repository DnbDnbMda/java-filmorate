package ru.yandex.practicum.filmorate.storage.dao;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        String sqlAllGenres = "select * from genres";
        return jdbcTemplate.query(sqlAllGenres, (rs, rowNum) -> mapToRowGenre(rs));
    }

    @Override
    public Genre getGenreById(int id) {
        String sqlGenreId = "select * from genres where genre_id = ?";
        List<Genre> genres = jdbcTemplate.query(sqlGenreId, (rs, rowNum) -> mapToRowGenre(rs), id);
        if (genres.isEmpty()) {
            throw new NotFoundException("Не существует жанра с таким id" + id);
        }
        return genres.get(0);
    }

    @Override
    public void setGenresToFilms(long filmId, List<Genre> genres) {
        List<Genre> unique = genres.stream().distinct().collect(Collectors.toList());
        genres.clear();
        genres.addAll(unique);

        String sqlSetFilmGenres = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        jdbcTemplate.batchUpdate(sqlSetFilmGenres, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setInt(2, genres.get(i).getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }

    @Override
    public List<Genre> getGenreByFilm(long filmId) {
        String sqlGenreFilm = "select g.* from FILM_GENRE as fg join GENRES as g on fg.genre_id = g.genre_id " +
                "where fg.film_id = ? ORDER BY g.GENRE_ID";
        return jdbcTemplate.query(sqlGenreFilm, (rs, rowNum) -> mapToRowGenre(rs), filmId);
    }

    @Override
    public List<Film> getGenresForFilm(List<Film> films) {
        Map<Long, Film> filmMap = new LinkedHashMap<>();
        films.forEach(film -> filmMap.put(film.getId(), film));
        Set<Long> filmIds = filmMap.keySet();

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("filmIds", filmIds);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sql = "SELECT g.genre_id, g.name, fg.film_id FROM genres g " +
                "INNER JOIN film_genre fg ON g.genre_id = fg.genre_id WHERE fg.film_id IN (:filmIds)";
        namedParameterJdbcTemplate.query(sql, sqlParameterSource, (rs, rowNum) -> {
            Film film = filmMap.get(rs.getLong("film_id"));
            return film.getGenres().add(mapToRowGenre(rs));
        });
        return new ArrayList<>(filmMap.values());
    }

    public Genre mapToRowGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("genre_id"),
                rs.getString("name"));
    }


}
