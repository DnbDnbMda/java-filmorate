package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FeedStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.Collection;
import java.util.Objects;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;
    private final FeedStorage feedStorage;

    @Autowired
    public ReviewServiceImpl(ReviewStorage reviewStorage,
                             UserService userService,
                             FilmService filmService, FeedStorage feedStorage) {
        this.reviewStorage = reviewStorage;
        this.userService = userService;
        this.filmService = filmService;
        this.feedStorage = feedStorage;
    }

    public Collection<Review> getAllReviews() {
        return reviewStorage.getAllReviews();
    }

    @Override
    public Review findReview(long reviewId) {
        Review review = reviewStorage.findReview(reviewId);

        if (Objects.isNull(review)) {
            throw new NotFoundException("Отзыв с Id = " + reviewId + " не найден.");
        }

        return review;
    }

    @Override
    public Collection<Review> getReviewByFilm(Long filmId, Integer count) {
        if (Objects.isNull(count)
                && Objects.isNull(filmId)) {
            return getAllReviews();
        }

        count = Objects.isNull(count) ? 10 : count;

        return reviewStorage.getReviewByFilm(filmId, count);
    }

    @Override
    public Review addReview(Review review) {
        userService.getUserById(review.getUserId());
        filmService.getFilmById(review.getFilmId());

        long reviewId = reviewStorage.addReview(review);

        feedStorage.addEntityToFeed(review.getUserId(), "ADD", "REVIEW", reviewId);

        return findReview(reviewId);
    }

    @Override
    public Review updateReview(Review review) {
        reviewStorage.updateReview(review);

        feedStorage.addEntityToFeed(review.getUserId(), "UPDATE", "REVIEW", review.getReviewId());

        return findReview(review.getReviewId());
    }

    @Override
    public void addLike(long id, long userId) {
        reviewStorage.addLike(id, userId);
    }

    @Override
    public void addDislike(long id, long userId) {
        reviewStorage.addDislike(id, userId);
    }

    @Override
    public void deleteReview(long id) {
        long userId = findReview(id).getUserId();

        reviewStorage.deleteReview(id);
        feedStorage.addEntityToFeed(userId, "REMOVE", "REVIEW", id);
    }

    @Override
    public void deleteLike(long id, long userId) {
        reviewStorage.deleteLike(id, userId);
    }

    @Override
    public void deleteDislike(long id, long userId) {
        reviewStorage.deleteDislike(id, userId);
    }
}
