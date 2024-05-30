
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private User user1;
    private User user2;
    private User user3;

    private UserService userService;

    @BeforeEach
    void setUp() {
        this.user1 = new User();
        user1.setEmail("email1@test.ru");
        user1.setLogin("test1");
        user1.setName("test_name1");
        user1.setBirthday(LocalDate.of(1997, Month.AUGUST,5));

        this.user2 = new User();
        user2.setEmail("email2@test.ru");
        user2.setLogin("test2");
        user2.setName("test_name2");
        user2.setBirthday(LocalDate.of(1997, Month.AUGUST,5));

        this.user3 = new User();
        this.userService = new UserService();
    }


    @Test
    void test_1ShouldCreateUser() {
        userService.createUser(user1);
        userService.createUser(user2);
        Collection<User> users = userService.getUsers();

        assertEquals(2,users.size());
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }

    @Test
    void test_2ShouldSetId() {
        userService.createUser(user1);
        assertEquals(1,user1.getId());
    }

    @Test
    @DisplayName("The Email field is empty")
    void test_3ShouldThrowAnValidationException() {
        user3.setLogin("test1");
        user3.setName("test_name1");
        user3.setBirthday(LocalDate.of(1997, Month.AUGUST,5));

        assertThrows(ValidationException.class, () -> {
            userService.createUser(user3);
        });
    }

    @Test
    @DisplayName("The Email field does not contain <@>")
    void test_4ShouldThrowAnValidationException() {
        user3.setEmail("email3test.ru");
        user3.setLogin("test3");
        user3.setName("test_name3");
        user3.setBirthday(LocalDate.of(1997, Month.AUGUST,5));

        assertThrows(ValidationException.class, () -> {
            userService.createUser(user3);
        });
    }

    @Test
    @DisplayName("The Login field is empty")
    void test_5ShouldThrowAnValidationException() {
        user3.setEmail("email3@test.ru");
        user3.setName("test_name3");
        user3.setBirthday(LocalDate.of(1997, Month.AUGUST,5));

        assertThrows(ValidationException.class, () -> {
            userService.createUser(user3);
        });
    }

    @Test
    void test_6ShouldSetName() {
        user3.setEmail("email3@test.ru");
        user3.setLogin("test3");
        user3.setName(" ");
        user3.setBirthday(LocalDate.of(1997, Month.AUGUST,5));
        userService.createUser(user3);

        assertEquals(user3.getLogin(), user3.getName());
    }

    @Test
    @DisplayName("The birthday field is later than the actual one")
    void test_7ShouldThrowAnValidationException() {
        user3.setEmail("email3@test.ru");
        user3.setLogin("test3");
        user3.setName("test_name3");
        user3.setBirthday(LocalDate.now().plusMonths(3));

        assertThrows(ValidationException.class, () -> {
            userService.createUser(user3);
        });
    }

    @Test
    void test_8ShouldUpdateUser() {
        userService.createUser(user1);
        userService.createUser(user2);
        int user1id = user1.getId();
        user3.setId(user1id);
        user3.setEmail("email3@test.ru");
        user3.setLogin("test3");
        user3.setName("test3_name");
        user3.setBirthday(LocalDate.of(1997, Month.AUGUST,5));

        userService.updateUser(user3);
        Collection<User> users = userService.getUsers();
        User updated = users.stream()
                .filter(user -> user.getId() == user1id)
                .findFirst()
                .orElse(null);

        assertEquals(user1.getName(), updated.getName());
    }

    @Test
    void test_9ShouldReturnUsers() {
        userService.createUser(user1);
        userService.createUser(user2);
        Collection<User> users = userService.getUsers();

        assertEquals(2, users.size());
    }
}
