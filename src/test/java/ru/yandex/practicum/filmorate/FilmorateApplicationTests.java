package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.MpaController;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.impl.FilmServiceImpl;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmorateApplicationTests {

    private final UserDbStorage userStorage;
    private final MpaDbStorage mpaDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenreDbStorage genreDbStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final LikesDbStorage likesDbStorage;
    private final FriendshipDbStorage friendshipDbStorage;
    private final DirectorDbStorage directorDbStorage;
    private final UserController userController;
    private final FilmController filmController;
    private final MpaController mpaController;
    private final ReviewController reviewController;
    private final FilmServiceImpl filmService;

    @Test
    public void testGetUserById() {
        User user1 = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser1 = userStorage.addUser(user1);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(addUser1.getId()));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", addUser1.getId())
                );
    }

    @Test
    public void testGetAllUsers() {
        User user1 = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser1 = userStorage.addUser(user1);
        User user2 = new User(2, "email2@mail.ru", "login2", "name2",
                LocalDate.of(1981, 8, 11));
        User addUser2 = userStorage.addUser(user2);
        List<User> allUsers = userStorage.getAllUsers();
        assertNotNull(allUsers);
        assertEquals(allUsers.size(), 2);
    }

    @Test
    public void testUpdateUser() {
        User user1 = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser1 = userStorage.addUser(user1);
        User updateUser1 = new User(1, "emailUpdate@email.ru", "loginUpdate", "nameUpdate",
                LocalDate.of(1991, 7, 11));
        userStorage.updateUser(updateUser1);
        assertNotEquals(addUser1.getEmail(), updateUser1.getEmail());
        assertEquals("emailUpdate@email.ru", updateUser1.getEmail());
        assertEquals("loginUpdate", updateUser1.getLogin());
        assertEquals("nameUpdate", updateUser1.getName());
    }

    @Test
    public void testAddUser() {
        User user1 = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser1 = userStorage.addUser(user1);
        assertThat(addUser1)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", addUser1.getId());
    }

    @Test
    public void testGetFilmById() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();

        Film addFilm1 = filmDbStorage.addFilm(film1);

        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilmById(addFilm1.getId()));

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", addFilm1.getId())
                );
    }

    @Test
    public void testAddFilm() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        Film addFilm1 = filmDbStorage.addFilm(film1);
        assertThat(addFilm1)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", addFilm1.getId());
    }

    @Test
    public void testUpdateFilm() {

        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        Film addFilm1 = filmDbStorage.addFilm(film1);
        Film updateFilm1 = Film.builder().id(1).name("Cредний фильм").description("Описание среднего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(123)
                .mpa(MpaRating.builder().id(1).build()).build();

        filmDbStorage.updateFilm(updateFilm1);

        assertThat(updateFilm1)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", updateFilm1.getId())
                .hasFieldOrPropertyWithValue("name", updateFilm1.getName());

        assertNotEquals(addFilm1.getName(), updateFilm1.getName());
        assertEquals("Cредний фильм", updateFilm1.getName());
        assertEquals("Описание среднего фильма", updateFilm1.getDescription());
    }

    @Test
    public void testGetAllFilms() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmDbStorage.addFilm(film1);
        Film film2 = Film.builder().id(2).name("Плохой фильм").description("Описание плохого фильма")
                .releaseDate(LocalDate.of(1999, 12, 12)).duration(160)
                .mpa(MpaRating.builder().id(3).build()).build();
        filmDbStorage.addFilm(film2);

        List<Film> allFilms = filmDbStorage.getAllFilms();
        assertNotNull(allFilms);
        assertEquals(allFilms.size(), 2);
    }

    @Test
    public void testGetAllGenres() {

        List<Genre> genres = genreDbStorage.getAllGenres();
        assertEquals(6, genres.size());
    }

    @Test
    public void testGetGenreById() {

        Optional<Genre> genreOptional = Optional.ofNullable(genreDbStorage.getGenreById(3));

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("id", 3)
                );

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(genre ->
                        assertThat(genre).hasFieldOrPropertyWithValue("name", "Мультфильм")
                );
    }

    @Test
    public void testGetGenreByFilmId() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmDbStorage.addFilm(film1);
        filmGenreStorage.addGenreToFilm(1, 2);
        filmGenreStorage.addGenreToFilm(1, 4);
        List<Genre> genres = genreDbStorage.getGenreByFilm(1);
        assertEquals(2, genres.size());
    }

    @Test
    public void testAddGenreToFilm() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmDbStorage.addFilm(film1);
        filmGenreStorage.addGenreToFilm(1, 2);

        List<Genre> genreToFilm = genreDbStorage.getGenreByFilm(1);
        assertNotNull(genreToFilm);
        assertEquals(genreToFilm.size(), 1);
    }

    @Test
    public void testRemoveGenresFromFilm() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmDbStorage.addFilm(film1);
        filmGenreStorage.addGenreToFilm(1, 2);
        filmGenreStorage.addGenreToFilm(1, 3);
        filmGenreStorage.addGenreToFilm(1, 4);

        filmGenreStorage.removeGenreFromFilm(1);
        List<Genre> genreToFilm = genreDbStorage.getGenreByFilm(1);

        assertThat(genreToFilm)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testGetAllMpa() {
        List<MpaRating> mpa = mpaDbStorage.getAllMpa();
        assertEquals(5, mpa.size());
    }

    @Test
    public void testGetMpaById() {
        Optional<MpaRating> mpaOptional = Optional.ofNullable(mpaDbStorage.getMpaById(3));

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpaRating ->
                        assertThat(mpaRating).hasFieldOrPropertyWithValue("id", 3)
                );

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpaRating ->
                        assertThat(mpaRating).hasFieldOrPropertyWithValue("name", "PG-13")
                );
    }

    @Test
    public void testAddLike() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmDbStorage.addFilm(film1);
        User user1 = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser1 = userStorage.addUser(user1);
        likesDbStorage.addLike(1, 1);
        List<Long> likesFilm = likesDbStorage.getLikesByFilm(1);
        assertNotNull(likesFilm);
    }

    @Test
    public void testDeleteLike() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmDbStorage.addFilm(film1);
        Film film2 = Film.builder().id(2).name("Плохой фильм").description("Описание плохого фильма")
                .releaseDate(LocalDate.of(1999, 12, 12)).duration(160)
                .mpa(MpaRating.builder().id(3).build()).build();
        filmDbStorage.addFilm(film2);
        User user1 = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser1 = userStorage.addUser(user1);
        User user2 = new User(2, "kotik@mail.ru", "kotik", "kate",
                LocalDate.of(1998, 7, 11));
        User addUser2 = userStorage.addUser(user2);
        likesDbStorage.addLike(1, 1);
        likesDbStorage.addLike(2, 2);

        likesDbStorage.deleteLike(1, 1);
        likesDbStorage.deleteLike(2, 2);
        List<Long> likesFilm = likesDbStorage.getLikesByFilm(1);

        assertThat(likesFilm)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testGetMostPopularFilms() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmDbStorage.addFilm(film1);
        Film film2 = Film.builder().id(2).name("Плохой фильм").description("Описание плохого фильма")
                .releaseDate(LocalDate.of(1999, 12, 12)).duration(160)
                .mpa(MpaRating.builder().id(3).build()).build();
        filmDbStorage.addFilm(film2);

        User user1 = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser1 = userStorage.addUser(user1);
        User user2 = new User(2, "kotik@mail.ru", "kotik", "kate",
                LocalDate.of(1998, 7, 11));
        User addUser2 = userStorage.addUser(user2);

        likesDbStorage.addLike(1, 2);
        likesDbStorage.addLike(1, 1);

        List<Film> popularFilms = filmDbStorage.getMostPopularFilms(1, null, null);
        assertEquals(1, popularFilms.size());
    }

    @Test
    public void testAddToFriend() {
        User user = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser = userStorage.addUser(user);
        User friend = new User(2, "kotik@mail.ru", "kotik", "kate",
                LocalDate.of(1998, 7, 11));
        User addFriend = userStorage.addUser(friend);

        friendshipDbStorage.addToFriend(1, 2);
        List<Long> friends = friendshipDbStorage.getAllFriendsByUser(1);
        assertNotNull(friends);
    }

    @Test
    public void testDeleteFromFriend() {
        User user = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser = userStorage.addUser(user);
        User friend = new User(2, "kotik@mail.ru", "kotik", "kate",
                LocalDate.of(1998, 7, 11));
        User addFriend = userStorage.addUser(friend);

        friendshipDbStorage.addToFriend(1, 2);
        friendshipDbStorage.deleteFromFriend(1, 2);
        List<Long> friends = friendshipDbStorage.getAllFriendsByUser(1);

        assertThat(friends)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testDeleteUser() {
        User user = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User friend = new User(2, "kotik@mail.ru", "kotik", "kate",
                LocalDate.of(1998, 7, 11));

        User addUser = userStorage.addUser(user);
        User addFriend = userStorage.addUser(friend);

        friendshipDbStorage.addToFriend(1, 2);

        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();

        filmDbStorage.addFilm(film1);
        likesDbStorage.addLike(1, 1);

        userStorage.deleteUser(addUser.getId());
        userStorage.deleteUser(addFriend.getId());

        List<Long> friendsDeleted = friendshipDbStorage.getAllFriendsByUser(1);
        List<Long> likesFilm = likesDbStorage.getLikesByFilm(1);

        assertEquals(0, friendsDeleted.size());
        assertEquals(0, likesFilm.size());
        assertEquals(0, userStorage.getAllUsers().size());
    }

    @Test
    public void testDeleteFilm() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();

        Film addFilm1 = filmDbStorage.addFilm(film1);

        filmGenreStorage.addGenreToFilm(1, 2);
        filmGenreStorage.addGenreToFilm(1, 4);

        User user = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));

        userStorage.addUser(user);
        likesDbStorage.addLike(1, 1);

        filmDbStorage.deleteFilm(addFilm1.getId());

        List<Genre> genres = genreDbStorage.getGenreByFilm(1);
        List<Long> likesFilm = likesDbStorage.getLikesByFilm(1);

        assertEquals(0, genres.size());
        assertEquals(0, likesFilm.size());
        assertEquals(filmDbStorage.getAllFilms().size(), 0);
    }

    @Test
    public void testAddReview() {
        Film film = filmController.addFilm(Film.builder().name("Film name").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        User user = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        Review review = reviewController.addReview(Review.builder().content("Good review").isPositive(true).userId(user.getId()).filmId(film.getId()).build());

        assertEquals(1, review.getReviewId());
        assertEquals(0, review.getUseful());
    }

    @Test
    public void testGetReview() {
        Film film = filmController.addFilm(Film.builder().name("Film name").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        User user = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        Review review = reviewController.addReview(Review.builder().content("Good review").isPositive(true).userId(user.getId()).filmId(film.getId()).build());

        assertEquals(1, reviewController.getReview(review.getReviewId()).getReviewId());
        assertThrows(NotFoundException.class, () -> reviewController.getReview(-1));
    }

    @Test
    public void testGetReviewByFilm() {
        Film film = filmController.addFilm(Film.builder().name("Film name").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        User user1 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        reviewController.addReview(Review.builder().content("Good review").isPositive(true).userId(user1.getId()).filmId(film.getId()).build());

        User user2 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        reviewController.addReview(Review.builder().content("Good review").isPositive(true).userId(user2.getId()).filmId(film.getId()).build());

        assertEquals(2, reviewController.getReviewByFilm(film.getId(), 2).size());
        assertEquals(2, reviewController.getReviewByFilm(null, null).size());
        assertEquals(1, reviewController.getReviewByFilm(null, 1).size());
    }

    @Test
    public void testUpdateReview() {
        Film film = filmController.addFilm(Film.builder().name("Film name").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        User user = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        Review review1 = reviewController.addReview(Review.builder().content("Good review").isPositive(true).userId(user.getId()).filmId(film.getId()).build());

        review1.setContent("Bad review");
        review1.setIsPositive(false);

        Review review2 = reviewController.updateReview(review1);

        assertEquals(review1.getReviewId(), review2.getReviewId());
        assertEquals(review2.getContent(), review1.getContent());
        assertEquals(review2.getIsPositive(), review1.getIsPositive());
    }

    @Test
    public void testAddReviewLike() {
        Film film = filmController.addFilm(Film.builder().name("Film name").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        User user1 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        Review review1 = reviewController.addReview(Review.builder().content("Good review").isPositive(true).userId(user1.getId()).filmId(film.getId()).build());
        User user2 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());

        reviewController.addLike(review1.getReviewId(), user1.getId());

        assertEquals(1, reviewController.getReview(review1.getReviewId()).getUseful());

        reviewController.addLike(review1.getReviewId(), user2.getId());

        assertEquals(2, reviewController.getReview(review1.getReviewId()).getUseful());
    }

    @Test
    public void testAddReviewDislike() {
        Film film = filmController.addFilm(Film.builder().name("Film name").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        User user1 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        Review review1 = reviewController.addReview(Review.builder().content("Good review").isPositive(true).userId(user1.getId()).filmId(film.getId()).build());
        User user2 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        User user3 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());

        reviewController.addDislike(review1.getReviewId(), user1.getId());

        assertEquals(-1, reviewController.getReview(review1.getReviewId()).getUseful());

        reviewController.addDislike(review1.getReviewId(), user2.getId());

        assertEquals(-2, reviewController.getReview(review1.getReviewId()).getUseful());

        reviewController.addLike(review1.getReviewId(), user3.getId());

        assertEquals(-1, reviewController.getReview(review1.getReviewId()).getUseful());
    }

    @Test
    public void testDeleteReview() {
        Film film1 = filmController.addFilm(Film.builder().name("Film name").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        Film film2 = filmController.addFilm(Film.builder().name("Film name").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        User user = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        Review review1 = reviewController.addReview(Review.builder().content("Good review").isPositive(true).userId(user.getId()).filmId(film1.getId()).build());
        reviewController.addReview(Review.builder().content("Good review").isPositive(true).userId(user.getId()).filmId(film2.getId()).build());

        assertEquals(2, reviewController.getReviewByFilm(null, null).size());

        reviewController.deleteReview(review1.getReviewId());

        assertEquals(1, reviewController.getReviewByFilm(null, null).size());
    }

    @Test
    public void testDeleteReviewLike() {
        Film film = filmController.addFilm(Film.builder().name("Film name").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        User user1 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        Review review1 = reviewController.addReview(Review.builder().content("Good review").isPositive(true).userId(user1.getId()).filmId(film.getId()).build());
        User user2 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());

        reviewController.addLike(review1.getReviewId(), user1.getId());
        reviewController.addLike(review1.getReviewId(), user2.getId());

        assertEquals(2, reviewController.getReview(review1.getReviewId()).getUseful());

        reviewController.deleteLike(review1.getReviewId(), user1.getId());

        assertEquals(1, reviewController.getReview(review1.getReviewId()).getUseful());
    }

    @Test
    public void testDeleteReviewDislike() {
        Film film = filmController.addFilm(Film.builder().name("Film name").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        User user1 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        Review review1 = reviewController.addReview(Review.builder().content("Good review").isPositive(true).userId(user1.getId()).filmId(film.getId()).build());
        User user2 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());

        reviewController.addDislike(review1.getReviewId(), user1.getId());
        reviewController.addDislike(review1.getReviewId(), user2.getId());

        assertEquals(-2, reviewController.getReview(review1.getReviewId()).getUseful());

        reviewController.deleteDislike(review1.getReviewId(), user1.getId());

        assertEquals(-1, reviewController.getReview(review1.getReviewId()).getUseful());
    }

    @Test
    public void addGetByIdUpdateDirector() {
        Director director = Director.builder().name("Director").build();
        Director addedDirector = directorDbStorage.addDirector(director);
        assertNotNull(addedDirector);
        assertEquals(1, addedDirector.getId());
        assertEquals(director.getName(), addedDirector.getName());

        Director recievedDirector = directorDbStorage.getDirectorById(addedDirector.getId());
        assertNotNull(recievedDirector);
        assertEquals(director.getName(), recievedDirector.getName());

        Director newDirector = Director.builder().id(1).name("New Director").build();
        Director updatedDirector = directorDbStorage.updateDirector(newDirector);

        assertNotNull(updatedDirector);
        assertEquals(newDirector.getName(), updatedDirector.getName());
    }

    @Test
    public void getAllDirectorsAndDelete() {
        Director director = Director.builder().name("Director").build();
        Director director1 = directorDbStorage.addDirector(director);
        Director newDirector = Director.builder().name("New Director").build();
        Director director2 = directorDbStorage.addDirector(newDirector);

        List<Director> directors = directorDbStorage.getAllDirectors();
        assertNotNull(directors);
        assertEquals(2, directors.size());
        assertEquals(director1.getName(), directors.get(0).getName());

        directorDbStorage.deleteDirector(1);
        List<Director> directorsAfterDelete = directorDbStorage.getAllDirectors();
        assertNotNull(directorsAfterDelete);
        assertEquals(1, directorsAfterDelete.size());
        assertEquals(director2.getName(), directorsAfterDelete.get(0).getName());
    }

    @Test
    public void getFilmsByDirector() {
        Director director = Director.builder().name("Director").build();
        Director director1 = directorDbStorage.addDirector(director);
        Film film1 = Film.builder().name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(1999, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        film1.getDirectors().add(director1);
        Film film2 = Film.builder().name("Новый фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        film2.getDirectors().add(director1);

        Film addedFilm1 = filmService.addFilm(film1);
        Film addedFilm2 = filmService.addFilm(film2);

        List<Film> filmsByYear = filmDbStorage.getFilmsByDirector(director1.getId(), "year");
        assertNotNull(filmsByYear);
        assertEquals(2, filmsByYear.size());
        assertEquals(film1.getName(), filmsByYear.get(0).getName());

        User user = User.builder().email("email@mail.ru").login("login1").name("name1")
                .birthday(LocalDate.of(1991, 7, 11)).build();
        User addedUser = userStorage.addUser(user);
        likesDbStorage.addLike(addedFilm2.getId(), addedUser.getId());

        List<Film> filmsByLikes = filmDbStorage.getFilmsByDirector(director1.getId(), "likes");
        assertNotNull(filmsByLikes);
        assertEquals(2, filmsByLikes.size());
        assertEquals(film2.getName(), filmsByLikes.get(0).getName());
    }

    @Test
    public void testGetUserFeed() {
        User user = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        userController.addUser(user);

        Film film = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmController.addFilm(film);

        Review review = reviewController.addReview(Review.builder().content("Good review")
                .isPositive(true).userId(user.getId()).filmId(film.getId()).build());

        review.setContent("Bad review");
        review.setIsPositive(false);

        reviewController.updateReview(review);
        reviewController.deleteReview(1);

        filmController.addLike(1, 1);
        filmController.deleteLike(1, 1);

        User friend = new User(2, "kotik@mail.ru", "kotik", "kate",
                LocalDate.of(1998, 7, 11));
        userController.addUser(friend);

        userController.addToFriend(1, 2);
        userController.deleteFromFriend(1, 2);

        Collection<Event> feed = userController.getUserFeed(1);

        assertEquals(7, feed.size());
    }

    @Test
    public void getFilmsByQuery() {
        Director director = Director.builder().name("Director f").build();
        Director director1 = directorDbStorage.addDirector(director);
        Film film1 = Film.builder().name("Film1").description("Desc1")
                .releaseDate(LocalDate.of(2022, 1, 1)).duration(90)
                .mpa(MpaRating.builder().id(4).build()).build();
        Film film2 = Film.builder().name("T2").description("sc2")
                .releaseDate(LocalDate.of(2000, 12, 8)).duration(150)
                .mpa(MpaRating.builder().id(1).build()).build();
        film2.getDirectors().add(director1);

        Film addedFilm1 = filmService.addFilm(film1);
        Film addedFilm2 = filmService.addFilm(film2);

        List<Film> films = filmDbStorage.getFilmsByQuery("Film", "title");
        System.out.println(addedFilm1);
        System.out.println(films);
        assertNotNull(films);
        assertEquals(1, films.size());
        assertEquals(addedFilm1.getId(), films.get(0).getId());
        assertEquals(addedFilm1.getName(), films.get(0).getName());

        films = filmDbStorage.getFilmsByQuery("F", "title,director");
        assertNotNull(films);
        assertEquals(2, films.size());
        assertEquals(addedFilm1.getId(), films.get(0).getId());
        assertEquals(addedFilm1.getName(), films.get(0).getName());
        assertEquals(addedFilm2.getId(), films.get(1).getId());
        assertEquals(addedFilm2.getName(), films.get(1).getName());

        films = filmDbStorage.getFilmsByQuery("tor", "director");
        assertNotNull(films);
        assertEquals(1, films.size());
        assertEquals(addedFilm2.getId(), films.get(0).getId());
        assertEquals(addedFilm2.getName(), films.get(0).getName());

        User user = User.builder().email("email@mail.ru").login("login1").name("name1")
                .birthday(LocalDate.of(1991, 7, 11)).build();
        User addedUser = userStorage.addUser(user);
        likesDbStorage.addLike(addedFilm2.getId(), addedUser.getId());

        films = filmDbStorage.getFilmsByQuery("f", "title,director");
        assertNotNull(films);
        assertEquals(2, films.size());
        assertEquals(addedFilm2.getId(), films.get(0).getId());
        assertEquals(addedFilm1.getId(), films.get(1).getId());

        films = filmDbStorage.getFilmsByQuery("3444", "title,director");
        assertEquals(0, films.size());
    }

    @Test
    public void testGetEmptyCommonFilms() {
        User user1 = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser1 = userStorage.addUser(user1);

        User user2 = new User(2, "email2@mail.ru", "login2", "name2",
                LocalDate.of(1992, 7, 12));
        User addUser2 = userStorage.addUser(user2);

        List<Film> commonFilms = filmDbStorage.getCommonFilms(addUser1.getId(), addUser2.getId());
        assertEquals(0, commonFilms.size());
    }

    @Test
    public void testGetEmptyCommonFilms2() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmDbStorage.addFilm(film1);

        Film film2 = Film.builder().id(2).name("Хороший фильм2").description("Описание хорошего фильма2")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmDbStorage.addFilm(film2);

        User user1 = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser1 = userStorage.addUser(user1);
        User user2 = new User(2, "email2@mail.ru", "login2", "name2",
                LocalDate.of(1992, 7, 12));
        User addUser2 = userStorage.addUser(user2);
        likesDbStorage.addLike(1, 1);
        likesDbStorage.addLike(2, 2);

        List<Film> commonFilms = filmDbStorage.getCommonFilms(addUser1.getId(), addUser2.getId());
        assertEquals(0, commonFilms.size());
    }

    @Test
    public void testGetCommonFilms() {

        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmDbStorage.addFilm(film1);

        Film film2 = Film.builder().id(2).name("Хороший фильм2").description("Описание хорошего фильма2")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).build()).build();
        filmDbStorage.addFilm(film2);

        User user1 = new User(1, "email@mail.ru", "login1", "name1",
                LocalDate.of(1991, 7, 11));
        User addUser1 = userStorage.addUser(user1);

        User user2 = new User(2, "email2@mail.ru", "login2", "name2",
                LocalDate.of(1992, 7, 12));
        User addUser2 = userStorage.addUser(user2);

        likesDbStorage.addLike(1, 1);
        likesDbStorage.addLike(1, 2);

        List<Film> commonFilms = filmDbStorage.getCommonFilms(addUser1.getId(), addUser2.getId());
        assertEquals(1, commonFilms.size());
    }

    @Test
    public void testGetRecommendation() {
        Film film1 = filmController.addFilm(Film.builder().name("Good").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        Film film2 = filmController.addFilm(Film.builder().name("Bad").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        Film film3 = filmController.addFilm(Film.builder().name("Evil").description("Film description").releaseDate(LocalDate.now()).duration(50).mpa(mpaController.getMpaById(1)).build());
        User user1 = userController.addUser(User.builder().email("Email@").login("Login").name("Name").birthday(LocalDate.now()).build());
        User user2 = userController.addUser(User.builder().email("Gmail@").login("Login").name("Name").birthday(LocalDate.now()).build());

        filmController.addLike(2, 1);
        filmController.addLike(3, 1);


        List<Film> films = userController.getRecommendation(2);
        assertTrue(films.isEmpty());

        filmController.addLike(1, 2);

        films = userController.getRecommendation(2);
        assertEquals(0, films.size());

        filmController.addLike(2, 2);
        films = userController.getRecommendation(1);

        assertEquals(1, films.size());
        assertEquals("Good", films.get(0).getName());

        filmController.deleteLike(1, 1);
        films = userController.getRecommendation(1);

        assertEquals(1, films.size());
        assertEquals("Good", films.get(0).getName());
    }


    @Test
    void getMostPopularFilmsTest() {
        Film film1 = Film.builder().id(1).name("Хороший фильм").description("Описание хорошего фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).name("G").build()).build();
        filmDbStorage.addFilm(film1);
        filmGenreStorage.addGenreToFilm(1, 1);

        Film film2 = Film.builder().id(2).name("Отличный фильм").description("Описание отличного фильма")
                .releaseDate(LocalDate.of(2000, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).name("G").build()).build();
        filmDbStorage.addFilm(film2);
        filmGenreStorage.addGenreToFilm(2, 2);

        Film film3 = Film.builder().id(3).name("Хороший фильм2").description("Описание хорошего фильма2")
                .releaseDate(LocalDate.of(2020, 12, 12)).duration(120)
                .mpa(MpaRating.builder().id(1).name("G").build()).build();
        filmDbStorage.addFilm(film3);
        filmGenreStorage.addGenreToFilm(3, 1);

        List<Film> findByGenreAndYear = filmDbStorage.getMostPopularFilms(10, 1, 2000);
        List<Film> findByGenre = filmDbStorage.getMostPopularFilms(10, 1, null);
        List<Film> findByYear = filmDbStorage.getMostPopularFilms(10, null, 2000);

        assertEquals(1, findByGenreAndYear.size());
        assertEquals(1, findByGenreAndYear.get(0).getId());
        assertEquals(2, findByGenre.size());
        assertNotNull(findByGenre.stream().filter(film -> film.getId() == 1).findFirst().orElse(null));
        assertNotNull(findByGenre.stream().filter(film -> film.getId() == 3).findFirst().orElse(null));
        assertEquals(2, findByYear.size());
        assertNotNull(findByYear.stream().filter(film -> film.getId() == 1).findFirst().orElse(null));
        assertNotNull(findByYear.stream().filter(film -> film.getId() == 2).findFirst().orElse(null));
    }
}