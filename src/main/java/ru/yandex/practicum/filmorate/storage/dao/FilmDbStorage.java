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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("releaseDate", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("mpaId", film.getMpa().getId());

        Number userKey = simpleJdbcInsert.executeAndReturnKey(new MapSqlParameterSource(values));
        film.setId(userKey.longValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlUpdFilm = "UPDATE FILMS SET NAME = :name, DESCRIPTION = :description, RELEASE_DATE = :releaseDate, " +
                "DURATION = :duration, MPA_ID = :mpaId WHERE FILM_ID = :id";
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("releaseDate", film.getReleaseDate())
                .addValue("duration", film.getDuration())
                .addValue("mpaId", film.getMpa().getId())
                .addValue("id", film.getId());
        int rowsAffected = namedParameterJdbcTemplate.update(sqlUpdFilm, sqlParameterSource);
        if (rowsAffected == 0) {
            throw new NotFoundException("Не существует фильма с id {}: " + film.getId());
        }

        return film;
    }

    @Override
    public Film getFilmById(long id) {
        String sqlFilmById = "SELECT * FROM FILMS f, MPA_RATING MR WHERE MR.MPA_ID = f.MPA_ID AND FILM_ID = ? ";
        List<Film> films = jdbcTemplate.query(sqlFilmById, (rs, rowNum) -> mapRowToFilm(rs), id);
        if (films.isEmpty()) {
            throw new NotFoundException(String.format("Фильм с id = %s не найден", id));
        }
        return films.get(0);
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlAllFilms = "SELECT f.*, m.name as mpa_name " +
                "FROM FILMS AS f JOIN MPA_RATING AS m ON f.MPA_ID = m.MPA_ID";
        return namedParameterJdbcTemplate.query(sqlAllFilms, (rs, rowNum) -> mapRowToFilm(rs));
    }

    @Override
    public List<Film> getMostPopularFilms(Integer count, Integer genreId, Integer year) {
        List<Object> parameters = new ArrayList<>();
        String sql = "SELECT f.*, mr.NAME " +
                "FROM FILMS AS f JOIN MPA_RATING AS mr ON f.MPA_ID = mr.MPA_ID " +
                "LEFT JOIN FILM_GENRE fg ON f.film_id = fg.film_id " +
                "LEFT JOIN (SELECT FILM_ID, COUNT(USER_ID) AS all_likes FROM LIKES GROUP BY FILM_ID ORDER BY all_likes) " +
                "   as toplist ON f.FILM_ID = toplist.FILM_ID " +
                "WHERE 1 = 1 ";
        if (genreId != null) {
            sql += "AND fg.genre_id = ? ";
            parameters.add(genreId);
        }
        if (year != null) {
            sql += "AND YEAR(f.release_date) = ? ";
            parameters.add(year);
        }

        String sqlPopularFilms = sql +
                " ORDER BY toplist.all_likes DESC LIMIT ?";
        parameters.add(count);
        return jdbcTemplate.query(sqlPopularFilms, (rs, rowNum) -> mapRowToFilm(rs), parameters.toArray());
    }

    @Override
    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        if (sortBy.equals("year")) {
            String sql = "SELECT f.*, mr.NAME FROM films as f " +
                    "JOIN MPA_RATING AS mr ON f.MPA_ID = mr.MPA_ID " +
                    "WHERE f.film_id IN (" +
                    "SELECT fd.film_id FROM film_director AS fd where fd.director_id = ?)" +
                    "ORDER BY f.release_date";
            return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), directorId);
        }
        String sql = "SELECT f.*, COUNT(l.user_id) rate, mr.name " +
                "FROM films as f " +
                "JOIN MPA_RATING AS mr ON f.MPA_ID = mr.MPA_ID " +
                "LEFT JOIN likes l on f.film_id = l.film_id " +
                "WHERE f.film_id IN (" +
                "SELECT fd.film_id FROM film_director AS fd where fd.director_id = ?) " +
                "GROUP BY f.film_id ORDER BY rate DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToFilm(rs), directorId);
    }

    @Override
    public List<Film> getFilmsByQuery(String query, String type) {
        StringBuilder sql = new StringBuilder("SELECT f.*, mr.NAME ")
                .append("FROM FILMS AS f ")
                .append("JOIN MPA_RATING AS mr ON f.MPA_ID = mr.MPA_ID ")
                .append("LEFT JOIN (SELECT FILM_ID, COUNT(USER_ID) AS all_likes FROM LIKES GROUP BY FILM_ID) AS toplist ")
                .append("ON f.FILM_ID = toplist.FILM_ID ")
                .append("LEFT JOIN FILM_DIRECTOR AS fd ON f.FILM_ID = fd.FILM_ID ")
                .append("LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.ID ");

        List<Object> params = new ArrayList<>();
        StringBuilder where = new StringBuilder();

        if (type.contains("title")) {
            where.append("LOWER(f.NAME) LIKE CONCAT('%', LOWER(?), '%')");
            params.add(query);
        }
        if (type.contains("director")) {
            if (where.length() > 0) {
                where.append(" OR ");
            }
            where.append("LOWER(d.NAME) LIKE CONCAT('%', LOWER(?), '%')");
            params.add(query);
        }
        if (where.length() > 0) {
            sql.append("WHERE ").append(where);
        }

        sql.append(" GROUP BY f.FILM_ID, mr.NAME ORDER BY COALESCE(toplist.all_likes, 0) DESC");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapRowToFilm(rs), params.toArray());
    }

    @Override
    public void deleteFilm(long id) {
        String sqlDelFilm = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlDelFilm, id);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        String sqlQuery =
                "SELECT * " +
                        "FROM FILMS AS f " +
                        "JOIN MPA_RATING AS m ON m.MPA_ID = f.MPA_ID " +
                        "JOIN LIKES AS l1 ON (l1.film_id = f.film_id AND l1.user_id = ?) " +
                        "JOIN LIKES AS l2 ON (l2.film_id = f.film_id AND l2.user_id = ?) " +
                        "JOIN (SELECT film_id, COUNT(user_id) AS rate " +
                        "FROM LIKES " +
                        "GROUP BY film_id) AS fl ON (fl.film_id = f.film_id) " +
                        "ORDER BY fl.rate DESC ";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToFilm(rs), userId, friendId);
    }

    @Override
    public List<Film> getRecommendation(long userId) {
        String sqlFilmsId = "SELECT l.film_id " +
                "FROM likes AS l " +
                "WHERE l.user_id IN (SELECT user_id " +
                "FROM likes AS l1 " +
                "WHERE l1.film_id IN (SELECT l2.film_id " +
                "FROM likes AS l2 " +
                "WHERE l2.user_id = ?) " +
                "AND l1.user_id <> ? " +
                "AND EXISTS (SELECT l3.film_id " +
                "FROM likes AS l3 WHERE l3.film_id NOT IN (SELECT l4.film_id  " +
                "FROM likes AS l4 " +
                "WHERE l4.user_id = ?)) " +
                "GROUP BY l1.user_id " +
                "ORDER BY COUNT(l.film_id) " +
                "LIMIT 1) " +
                "GROUP BY l.film_id " +
                "HAVING l.film_id NOT IN (SELECT l5.film_id " +
                "FROM likes AS l5 " +
                "WHERE l5.user_id = ?) " +
                "ORDER BY l.film_id";

        Set<Long> filmsId = new HashSet<>(jdbcTemplate.query(sqlFilmsId, (rs, rowNum) -> rs.getLong("film_id"),
                userId, userId, userId, userId));

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("filmsId", filmsId);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        String sqlFilms = "SELECT * FROM films AS f " +
                "LEFT JOIN  mpa_rating AS r ON f.mpa_id = r.mpa_id " +
                "WHERE f.film_id IN (:filmsId)";

        return namedParameterJdbcTemplate.query(sqlFilms, sqlParameterSource, (rs, rowNum) -> mapRowToFilm(rs));
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new MpaRating(rs.getInt("mpa_id"), rs.getString("MPA_RATING.NAME"), null))
                .build();
    }
}