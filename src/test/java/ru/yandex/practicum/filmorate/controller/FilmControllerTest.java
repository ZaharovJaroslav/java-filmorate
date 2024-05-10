package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private Film film1;
    private Film film2;
    private Film film3;
   private FilmController filmController;

    @BeforeEach
    void setUp() {
        this.film1 = new Film();
        film1.setName("film1_name");
        film1.setDescription("film1_description");
        film1.setReleaseDate(LocalDate.of(1999,Month.DECEMBER,12));
        film1.setDuration(190);

        this.film2 = new Film();
        film2.setName("film2_name");
        film2.setDescription("film2_description");
        film2.setReleaseDate(LocalDate.of(2005,Month.MARCH,1));
        film2.setDuration(200);

        this.film3 = new Film();
        this.filmController = new FilmController();
    }

    @Test
    void test_1ShouldAddFilm() {
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        Collection<Film> films = new ArrayList<>(filmController.getFilms());
        assertEquals(2, films.size());
        assertTrue(films.contains(film1));
        assertTrue(films.contains(film2));
    }

    @Test
    void test_2ShouldSetId() {
        film3.setName("film3_name");
        film3.setDescription("film3_description");
        film3.setReleaseDate(LocalDate.of(1998,Month.DECEMBER,29));
        film3.setDuration(190);
        filmController.validationFilm(film3);

        assertEquals(1, film3.getId());
    }

    @Test
    @DisplayName("The name field is not set")
    void test_3ShouldThrowAnValidationException() {
        film3.setDescription("film3_description");
        film3.setReleaseDate(LocalDate.of(1998,Month.DECEMBER,29));
        film3.setDuration(190);

        assertThrows(ValidationException.class, () -> {
            filmController.validationFilm(film3);
        });
    }

    @Test
    @DisplayName("The description field contains more than 200 characters")
    void test_4ShouldThrowAnValidationException() {
        film3.setName("film3_name");
        film3.setDescription("Ruskin calls books, \"Kings' Treasures\" -- treasuries filled, not with gold and silver" +
                "and precious stones, but with riches much more valuable than these -- knowledge, noble thoughts" +
                " and high ideals.......................");
        film3.setReleaseDate(LocalDate.of(1998,Month.DECEMBER,29));
        film3.setDuration(190);

        assertThrows(ValidationException.class, () -> {
            filmController.validationFilm(film3);
        });
    }

    @Test
    @DisplayName("The ReleaseDate field is earlier than 12/28/1895")
    void test_5ShouldThrowAnValidationException() {
        film3.setName("film3_name");
        film3.setDescription("film3_description");
        film3.setReleaseDate(LocalDate.of(1895,Month.DECEMBER,27));
        film3.setDuration(190);

        assertThrows(ValidationException.class, () -> {
            filmController.validationFilm(film3);
        });
    }

    @Test
    @DisplayName("The duration field contains a negative number")
    void test_6ShouldThrowAnValidationException() {
        film3.setName("film3_name");
        film3.setDescription("film3_description");
        film3.setReleaseDate(LocalDate.of(1998,Month.DECEMBER,29));
        film3.setDuration(-190);

        assertThrows(ValidationException.class, () -> {
            filmController.validationFilm(film3);
        });
    }

    @Test
    void test_7ShouldUpdateFilm() {
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        int film1id = film1.getId();
        film3.setId(film1id);
        film3.setName("film3_name");
        film3.setDescription("film3_description");
        film3.setReleaseDate(LocalDate.of(1998,Month.DECEMBER,29));
        film3.setDuration(190);

        filmController.updateFilm(film3);
        Collection<Film> films = filmController.getFilms();
        Film updated = films.stream()
                .filter(film -> film.getId() == film1id)
                .findFirst()
                .orElse(null);

        assertEquals(film3.getName(), updated.getName());
        }

    @Test
    void test_8ShouldReturnFilms() {
        filmController.addFilm(film1);
        filmController.addFilm(film2);
        Collection<Film> films = filmController.getFilms();

        assertEquals(2, films.size());
    }
}