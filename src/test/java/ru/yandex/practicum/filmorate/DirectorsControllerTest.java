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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorsService;

import java.util.Collection;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DirectorsControllerTest {
    private final DirectorsService directorsService;
    private final JdbcTemplate jdbcTemplate;

    private final Director director1 = new Director(1L, "Director One");
    private final Director director2 = new Director(2L, "Director Two");

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("DELETE FROM film_directors");
        jdbcTemplate.execute("DELETE FROM directors");
    }

    //добавление нового режиссера и проверяет, что режиссер был успешно добавлен
    @Test
    void createDirector_shouldAddDirector() {
        Director newDirector = directorsService.createDirector(director1);

        Optional<Director> retrievedDirector = directorsService.getDirectorById(newDirector.getId());
        Assertions.assertTrue(retrievedDirector.isPresent());
        Assertions.assertEquals(newDirector.getName(), retrievedDirector.get().getName());
    }

    //получение всех режиссеров
    @Test
    void getAllDirectors_shouldReturnListOfDirectors() {
        directorsService.createDirector(director1);
        directorsService.createDirector(director2);

        Collection<Director> directors = directorsService.getAllDirectors();
        Assertions.assertEquals(2, directors.size());
    }

    //получение режиссера по ID
    @Test
    void getDirectorById_shouldReturnDirector() {
        Director newDirector = directorsService.createDirector(director1);

        Optional<Director> retrievedDirector = directorsService.getDirectorById(newDirector.getId());
        Assertions.assertTrue(retrievedDirector.isPresent());
        Assertions.assertEquals(newDirector.getName(), retrievedDirector.get().getName());
    }

    //выбрасывается исключение, если режиссер не найден
    @Test
    void getDirectorById_shouldThrowExceptionIfNotFound() {
        long nonExistentId = 999L;
        Assertions.assertThrows(NotFoundException.class, () -> {
            directorsService.getDirectorById(nonExistentId).orElseThrow(() -> new NotFoundException("Режиссер не найден"));
        });
    }

    //обновление данных режиссера
    @Test
    void updateDirector_shouldUpdateDirector() {
        Director newDirector = directorsService.createDirector(director1);
        newDirector.setName("Режиссер обновлён");

        Director updatedDirector = directorsService.updateDirector(newDirector);
        Assertions.assertEquals("Режиссер обновлён", updatedDirector.getName());
    }

    //выбрасывается исключение, если обновляемый режиссер не найден
    @Test
    void updateDirector_shouldThrowExceptionIfNotFound() {
        Director nonExistentDirector = new Director(999L, "Non-existent Director");
        Assertions.assertThrows(NotFoundException.class, () -> {
            directorsService.updateDirector(nonExistentDirector);
        });
    }

    //удаление режиссера
    @Test
    void deleteDirector_shouldDeleteDirector() {
        Director newDirector = directorsService.createDirector(director1);
        boolean isDeleted = directorsService.deleteDirector(newDirector.getId());
        Assertions.assertTrue(isDeleted);

        Optional<Director> retrievedDirector = directorsService.getDirectorById(newDirector.getId());
        Assertions.assertFalse(retrievedDirector.isPresent());
    }

    //метод возвращает false, если удаляемый режиссер не найден
    @Test
    void deleteDirector_shouldReturnFalseIfNotFound() {
        long nonExistentId = 999L;
        boolean isDeleted = directorsService.deleteDirector(nonExistentId);
        Assertions.assertFalse(isDeleted);
    }
}
