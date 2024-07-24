package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.DirectorsService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTest {
    private final FilmService filmService;
    private final UserService userService;
    private final DirectorsService directorsService;
    private final JdbcTemplate jdbcTemplate;
    private final User user = new User("user@ya.ru", "flying_dragon", "Andrew",
            LocalDate.of(1996, 12, 3));
    private final Film film = new Film("Ron's Gone Wrong", "The cartoon about a funny robot",
            LocalDate.of(2021, 10, 22), 107);
    private final Film updatedFilm = new Film("Ron's Gone Wrong",
            "The cartoon about a friendship between robot and a human",
            LocalDate.of(2021, 10, 22), 107);
    private final Film oneMoreFilm = new Film("Red Lights", "The misctic movie about prycology",
            LocalDate.of(2012, 1, 20), 113);
    private final Film unexistingFilm = new Film("Sowl", "jbdbfglkawng",
            LocalDate.of(2023, 2, 2), 100);
    private final Film popularFilm = new Film("Titanic",
            "The movie about love", LocalDate.of(1997, 12, 19), 220);

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM directors");
    }

    @Test
    public void addFilm_shouldaddFilm() {
        film.setMpa(new Mpa(1));
        film.setGenres(List.of(new Genre(2)));
        filmService.addFilm(film);

        Assertions.assertFalse(filmService.getFilms().isEmpty());
    }

    @Test
    public void addFilm_shouldNotaddFilmIfDescriptionTooLong() {
        film.setMpa(new Mpa(1));
        film.setGenres(List.of(new Genre(2)));
        film.setDescription("This is the cartoon about a funny robot who always get into trouble." +
                "Other people though he is defective, but there was a guy who was happy to get him on his birthday." +
                "This cartoon show the future behaviour of our generation who grew up next to the phone and computer.");

        Assertions.assertThrows(ValidationException.class, () -> filmService.addFilm(film));
    }

    @Test
    public void updateFilm_shouldUpdateFilm() {
        film.setMpa(new Mpa(1));
        film.setGenres(List.of(new Genre(1)));
        Film newFilm = filmService.addFilm(film);
        newFilm.setGenres(List.of(new Genre(3), new Genre(2)));
        Film filmUpdated = filmService.updateFilm(newFilm);

        Assertions.assertEquals(filmService.getFilmById(newFilm.getId()).getName(),
                filmService.getFilmById(filmUpdated.getId()).getName());
    }

    @Test
    public void getFilmById_shouldReturnFilm() {
        film.setMpa(new Mpa(1));
        film.setGenres(List.of(new Genre(1)));
        Film newFilm = filmService.addFilm(film);

        Assertions.assertEquals(newFilm, filmService.getFilmById(newFilm.getId()));
    }

    @Test
    public void getFilmById_shouldNotReturnFilmIfIdIsIncorrect() {
        Assertions.assertThrows(NotFoundException.class, () -> filmService.getFilmById(145));
    }

    @Test
    public void getFilms_shouldReturnListOfFilms() {
        film.setMpa(new Mpa(1));
        film.setGenres(List.of(new Genre(1)));
        filmService.addFilm(film);
        popularFilm.setMpa(new Mpa(1));
        popularFilm.setGenres(List.of(new Genre(1), new Genre(2)));
        filmService.addFilm(popularFilm);

        Assertions.assertEquals(2, filmService.getFilms().size());
    }

    @Test
    public void getFilms_shouldReturnAnEmptyListOfFilms() {
        Assertions.assertTrue(filmService.getFilms().isEmpty());
    }

    @Test
    public void getPopularMovies_shouldReturnListOfPopularMovies() {
        User newUser = userService.createUser(user);
        updatedFilm.setMpa(new Mpa(3));
        updatedFilm.setGenres(List.of(new Genre(1), new Genre(2)));
        Film newFilm = filmService.addFilm(updatedFilm);
        popularFilm.setMpa(new Mpa(4));
        popularFilm.setGenres(List.of(new Genre(2), new Genre(3)));
        Film likedMovie = filmService.addFilm(popularFilm);
        filmService.addLike(likedMovie.getId(), newUser.getId());
        Collection<Film> films = filmService.getPopularMoviesByLikes(1, Optional.empty(), Optional.empty());

        Assertions.assertTrue(films.contains(likedMovie));
    }

    @Test
    public void like_shouldLikeAMovie() {
        User newUser = userService.createUser(user);
        unexistingFilm.setMpa(new Mpa(3));
        unexistingFilm.setGenres(List.of(new Genre(1), new Genre(2)));
        Film newFilm = filmService.addFilm(unexistingFilm);
        filmService.addLike(newFilm.getId(), newUser.getId());

        Assertions.assertEquals(1, filmService.getPopularMoviesByLikes(1,Optional.empty(),Optional.empty()).size());
    }

    @Test
    public void dislike_shouldNotDislikeAMovieIfItWasNotLiked() {
        userService.createUser(user);
        film.setMpa(new Mpa(3));
        film.setGenres(List.of(new Genre(1), new Genre(2)));
        filmService.addFilm(film);

        Assertions.assertThrows(NotFoundException.class,
                () -> filmService.dislike(film.getId(), user.getId()));
    }

    @Test
    void searchFilms_shouldReturnFilmsByTitle() {
        film.setMpa(new Mpa(1));
        film.setGenres(List.of(new Genre(1)));
        filmService.addFilm(film);

        List<Film> films = filmService.searchFilms("Ron's", new String[]{"title"});

        Assertions.assertFalse(films.isEmpty());
        Assertions.assertTrue(films.stream().allMatch(f -> f.getName().toLowerCase().contains("ron's".toLowerCase())));
    }

    @Test
    void searchFilms_shouldReturnFilmsByDirector() {
        film.setMpa(new Mpa(1));
        film.setGenres(List.of(new Genre(1)));
        Director director = new Director(1, "Director Name");
        Director createdDirector = directorsService.createDirector(director);
        film.setDirectors(List.of(createdDirector));
        filmService.addFilm(film);

        List<Film> films = filmService.searchFilms("Director", new String[]{"director"});

        Assertions.assertFalse(films.isEmpty());
        Assertions.assertTrue(films.stream().anyMatch(f -> f.getDirectors().stream()
                .anyMatch(d -> d.getName().toLowerCase().contains("director".toLowerCase()))));
    }

    @Test
    void searchFilms_shouldReturnFilmsByTitleAndDirector() {
        film.setMpa(new Mpa(1));
        film.setGenres(List.of(new Genre(1)));
        Director director = new Director(1, "Director Name");
        Director createdDirector = directorsService.createDirector(director);
        film.setDirectors(List.of(createdDirector));
        filmService.addFilm(film);

        List<Film> films = filmService.searchFilms("Ron's", new String[]{"title", "director"});

        Assertions.assertFalse(films.isEmpty());
        Assertions.assertTrue(films.stream().anyMatch(f -> f.getName().toLowerCase().contains("ron's".toLowerCase()) ||
                f.getDirectors().stream().anyMatch(d -> d.getName().toLowerCase().contains("ron's".toLowerCase()))));
    }
}
