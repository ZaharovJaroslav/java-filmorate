package ru.yandex.practicum.filmorate.storage.db.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mapper.UserMapper;

import java.sql.Date;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User createUser(User user) {
        log.debug("createUser({})", user);
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) "
                        + "VALUES (?, ?, ?, ?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()));
        User thisUser = jdbcTemplate.queryForObject(
                "SELECT user_id, email, login, name, birthday "
                        + "FROM users "
                        + "WHERE email=?", new UserMapper(), user.getEmail());
        log.trace("{} пользователь был добавлен в базу данных", thisUser);
        return thisUser;
    }


    @Override
    public User updateUser(User user) {
        log.debug("updateUser({})", user);
        jdbcTemplate.update("UPDATE users "
                        + "SET email=?, login=?, name=?, birthday=? "
                        + "WHERE user_id=?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        Optional<User> thisUser = getUserById(user.getId());
        if (thisUser.isPresent()) {
            log.trace("Имя пользователя {} было обновлено в базе данных", thisUser);
            return thisUser.get();
        } else
            log.trace("не удалось обноыить пользователя с id {}", user.getId());
        return user;
    }

    @Override
    public void deleteUserById(int id) {
        log.debug("deleteUserById({})", id);
        jdbcTemplate.update("DELETE FROM users WHERE user_id=?", id);

       if (getUserById(id).isPresent()) {
           log.trace("Пользователь с id = {} удален",id);
       } else
           log.debug("Не удалось удалить пользователся с id = {}", id);
    }

    @Override
    public Collection<User> getUsers() {
        log.debug("getUsers()");
        Collection<User> users = jdbcTemplate.query(
                "SELECT user_id, email, login, name, birthday FROM users ",
                new UserMapper());
        log.trace("Это пользователи в базе данных: : {}", users);
        return  users;
    }

    @Override
    public Optional<User> getUserById(int id) {
        log.debug("getUserById({})", id);
        try {
            User thisUser = jdbcTemplate.queryForObject(
                    "SELECT user_id, email, login, name, birthday FROM users "
                            + "WHERE user_id=?", new UserMapper(), id);
            log.trace("Пользователь {} был возвращен", thisUser);
            return Optional.ofNullable(thisUser);

        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    @Override
    public void checkNotExsistUser(int userId) {

    }
}
