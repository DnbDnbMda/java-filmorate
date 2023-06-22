package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final FilmGenreDbStorage filmGenreDbStorage;

    public FilmDbStorage(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate, GenreStorage genreStorage, FilmGenreDbStorage filmGenreDbStorage) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.genreStorage = genreStorage;
        this.filmGenreDbStorage = filmGenreDbStorage;
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
    public List<Film> getMostPopularFilms(Integer count) {
        String sqlPopularFilms = "SELECT f.*, mr.NAME " +
                "FROM FILMS AS f JOIN MPA_RATING AS mr ON f.MPA_ID = mr.MPA_ID " +
                "LEFT JOIN (SELECT FILM_ID, COUNT(USER_ID) AS all_likes FROM LIKES GROUP BY FILM_ID ORDER BY all_likes) " +
                "as toplist ON f.FILM_ID = toplist.FILM_ID ORDER BY toplist.all_likes DESC LIMIT ?";
        return jdbcTemplate.query(sqlPopularFilms, (rs, rowNum) -> mapRowToFilm(rs), count);
    }

    @Override
    public void deleteFilm(long id) {
        String sqlDelFilm = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlDelFilm, id);
    }

    private Film mapRowToFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        int mpaId = rs.getInt("mpa_id");
        String mpaName = rs.getString("MPA_RATING.NAME");

        MpaRating mpa = MpaRating.builder()
                .id(mpaId)
                .name(mpaName)
                .build();

        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .mpa(mpa)
                .build();
    }

    @Override
    public Collection<Film> getCommonFilms(long userId, long friendId) {

        String sqlQuery =
                "SELECT" +
                        "    *" +
                        "FROM" +
                        "    FILMS AS f" +
                        "JOIN" +
                        "    MPA AS m" +
                        "        ON m.MPA_ID = f.MPA_ID" +
                        "JOIN" +
                        "    LIKES AS l1" +
                        "        ON (" +
                        "            l1.film_id = f.film_id" +
                        "            AND l1.user_id = ?" +
                        "        )" +
                        "JOIN" +
                        "    LIKES AS l2" +
                        "        ON (" +
                        "            l2.film_id = f.film_id" +
                        "            AND l2.user_id = ?" +
                        "        )" +
                        "JOIN" +
                        "    (" +
                        "        SELECT" +
                        "            film_id," +
                        "            COUNT(user_id) AS rate" +
                        "        FROM" +
                        "            LIKES" +
                        "        GROUP BY" +
                        "            film_id" +
                        "    ) AS fl" +
                        "        ON (" +
                        "            fl.film_id = f.film_id" +
                        "        )" +
                        "ORDER BY" +
                        "    fl.rate DESC";
        return jdbcTemplate.query(sqlQuery, (rs, rowNum) -> mapRowToFilm(rs), userId, friendId);
    }
}


  /*  DROP TABLE IF EXISTS LIKES CASCADE;
    create table IF NOT EXISTS LIKES
        (
                FILM_ID int NOT NULL,
                USER_ID int NOT NULL,
                constraint FK_LIKES_FILMID
        foreign key (FILM_ID) references FILMS (FILM_ID)
        on delete cascade,
        constraint FK_LIKES_USERID
        foreign key (USER_ID) references USERS (USER_ID) on delete cascade
        );*/
