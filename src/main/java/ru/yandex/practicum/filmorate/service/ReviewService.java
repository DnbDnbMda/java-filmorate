package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewService {
    Review findReview(long reviewId);

    Collection<Review> getReviewByFilm(Long filmId, Integer count);

    Review addReview(Review review);

    Review updateReview(Review review);

    void addLike(long id, long userId);

    void addDislike(long id, long userId);

    void deleteReview(long id);

    void deleteLike(long id, long userId);

    void deleteDislike(long id, long userId);
}
