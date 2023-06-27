package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Review getReview(@PathVariable(name = "id") long reviewId) {
        return reviewService.findReview(reviewId);
    }

    @GetMapping
    public Collection<Review> getReviewByFilm(@RequestParam(name = "filmId", required = false) Long filmId,
                                              @RequestParam(name = "count", required = false) Integer count) {
        return reviewService.getReviewByFilm(filmId, count);
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable(name = "id") long id,
                        @PathVariable(name = "userId") long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable(name = "id") long id,
                           @PathVariable(name = "userId") long userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable(name = "id") long id) {
        reviewService.deleteReview(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable(name = "id") long id,
                           @PathVariable(name = "userId") long userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable(name = "id") long id,
                              @PathVariable(name = "userId") long userId) {
        reviewService.deleteDislike(id, userId);
    }
}
