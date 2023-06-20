package ru.yandex.practicum.filmorate.storage.dao;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@AllArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String SELECTION_STRING = "SELECT REVIEWS.*, " +
            "(SELECT count(*) FROM REVIEW_LIKES WHERE REVIEW_LIKES.REVIEW_ID = REVIEWS.REVIEW_ID AND REVIEW_LIKES.IS_USEFUL = true) - " +
            "(SELECT count(*) FROM REVIEW_LIKES WHERE REVIEW_LIKES.REVIEW_ID = REVIEWS.REVIEW_ID AND REVIEW_LIKES.IS_USEFUL = false) AS USEFUL " +
            "FROM REVIEWS AS REVIEWS ";

    @Override
    public Collection<Review> getAllReviews() {
        String sql = SELECTION_STRING +
                "ORDER BY USEFUL DESC, REVIEW_ID ASC;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public Review findReview(long reviewId) {
        String sql = SELECTION_STRING +
                "WHERE REVIEWS.REVIEW_ID = ? " +
                "ORDER BY USEFUL DESC, REVIEW_ID ASC " +
                "LIMIT 1;";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), reviewId).stream().findFirst().orElse(null);
    }

    @Override
    public Collection<Review> getReviewByFilm(Long filmId, Integer count) {
        StringBuilder stringBuilder = new StringBuilder().append(SELECTION_STRING);
        if (!Objects.isNull(filmId)) {
            stringBuilder.append("WHERE REVIEWS.FILM_ID = ? ");
        }
        stringBuilder.append("ORDER BY USEFUL DESC, REVIEW_ID ASC ");
        stringBuilder.append("LIMIT ").append(count).append(";");

        if (!Objects.isNull(filmId)) {
            return jdbcTemplate.query(stringBuilder.toString(), (rs, rowNum) -> makeReview(rs), filmId);
        }

        return jdbcTemplate.query(stringBuilder.toString(), (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public long addReview(Review review) {
        SimpleJdbcInsert insertIntoUser = new SimpleJdbcInsert(jdbcTemplate).withTableName("REVIEWS").usingGeneratedKeyColumns("REVIEW_ID");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("CONTENT", review.getContent());
        parameters.put("IS_POSITIVE", review.getIsPositive());
        parameters.put("USER_ID", review.getUserId());
        parameters.put("FILM_ID", review.getFilmId());

        return insertIntoUser.executeAndReturnKey(parameters).intValue();
    }

    @Override
    public void updateReview(Review review) {
        jdbcTemplate.update("UPDATE REVIEWS " +
                        "SET CONTENT = ?, " +
                        "IS_POSITIVE = ? " +
                        "WHERE REVIEW_ID = ?;",
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
    }

    @Override
    public void addLike(long id, long userId) {
        String sql = "MERGE INTO REVIEW_LIKES AS target " +
                "USING (SELECT CONVERT(?, INT) AS REVIEW_ID, CONVERT(?, INT) AS USER_ID) AS source_table " +
                "ON (target.REVIEW_ID = source_table.REVIEW_ID AND target.USER_ID = source_table.USER_ID) " +
                "WHEN MATCHED " +
                "THEN UPDATE SET " +
                "target.IS_USEFUL = true " +
                "WHEN NOT MATCHED " +
                "THEN INSERT (REVIEW_ID, USER_ID, IS_USEFUL) " +
                "VALUES (source_table.REVIEW_ID, source_table.USER_ID, true);";

        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void addDislike(long id, long userId) {
        String sql = "MERGE INTO REVIEW_LIKES AS target " +
                "USING (SELECT CONVERT(?, INT) AS REVIEW_ID, CONVERT(?, INT) AS USER_ID) AS source_table " +
                "ON (target.REVIEW_ID = source_table.REVIEW_ID AND target.USER_ID = source_table.USER_ID) " +
                "WHEN MATCHED " +
                "THEN UPDATE SET " +
                "target.IS_USEFUL = false " +
                "WHEN NOT MATCHED " +
                "THEN INSERT (REVIEW_ID, USER_ID, IS_USEFUL) " +
                "VALUES (source_table.REVIEW_ID, source_table.USER_ID, false);";

        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteReview(long id) {
        String sql = "DELETE FROM REVIEWS " +
                "WHERE REVIEW_ID = ?;";

        jdbcTemplate.update(sql, id);
    }

    @Override
    public void deleteLike(long id, long userId) {
        String sql = "DELETE FROM REVIEW_LIKES " +
                "WHERE REVIEW_ID = ? " +
                "AND USER_ID = ? " +
                "AND IS_USEFUL = true;";

        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteDislike(long id, long userId) {
        String sql = "DELETE FROM REVIEW_LIKES " +
                "WHERE REVIEW_ID = ? " +
                "AND USER_ID = ? " +
                "AND IS_USEFUL = false;";

        jdbcTemplate.update(sql, id, userId);
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("REVIEW_ID"))
                .content(rs.getString("CONTENT"))
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .userId(rs.getLong("USER_ID"))
                .filmId(rs.getLong("FILM_ID"))
                .useful(rs.getInt("USEFUL"))
                .build();
    }
}
