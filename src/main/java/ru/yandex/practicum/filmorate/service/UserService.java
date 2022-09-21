package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final DateTimeFormatter dateTimeFormatter;

    @Autowired
    public UserService(InMemoryUserStorage userStorage) {
        this.userStorage = userStorage;
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int userId) {
        if (userStorage.checkIsUserInStorage(userId)) {
            return userStorage.getUserById(userId);
        } else {
            log.info("User id={} not found", userId);
            throw new ObjectNotFoundException(String.format("User id=%s not found", userId));
        }
    }

    public User addUser(User newUser) {
        if (checkIsUserDataCorrect(newUser)) {
            return userStorage.addUser(newUser);
        } else return null;
    }

    public User updateUser(User updatedUser) {
        if (userStorage.checkIsUserInStorage(updatedUser)) {
            if (checkIsUserDataCorrect(updatedUser)) {
                return userStorage.updateUser(updatedUser);
            } else return null;
        } else {
            log.info("User id={} not found", updatedUser.getId());
            throw new ObjectNotFoundException(String.format("Failed to updte user data id=%s т.к. " +
                    "user not found", updatedUser.getId()));
        }
    }

    public void addFriend(int userId, int friendId) {
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("User id={} not found", userId);
            throw new ObjectNotFoundException(String.format("User id=%s not found", userId));
        }
        if (!userStorage.checkIsUserInStorage(friendId)) {
            log.info("User id={} not found", friendId);
            throw new ObjectNotFoundException(String.format("User id=%s not found", friendId));
        }
        userStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("User id={} not found", userId);
            throw new ObjectNotFoundException(String.format("User id=%s not found", userId));
        }
        if (!userStorage.checkIsUserInStorage(friendId)) {
            log.info("User id={} not found", friendId);
            throw new ObjectNotFoundException(String.format("User id=%s not found", friendId));
        }
        if (!userStorage.checkAreTheseUsersFriends(userId, friendId)) {
            log.info("User id={} not in the user's friends list id={}", friendId, userId);
            throw new ObjectNotFoundException(String.format("User id=%s not in the user's friends list id=%s",
                    friendId, userId));
        }
        if (!userStorage.checkAreTheseUsersFriends(friendId, userId)) {
            log.info("User id={} not in the user's friends list id={}", userId, friendId);
            throw new ObjectNotFoundException(String.format("User id=%s not in the user's friends list id=%s",
                    userId, friendId));
        }
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getListOfFriends(int userId) {
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("User id={} not found", userId);
            throw new ObjectNotFoundException(String.format("User id=%s not found", userId));
        }
        return userStorage.getListOfFriends(userId);
    }

    public List<User> getListOfCommonFriends(int userId, int friendId) {
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("User id={} not found", userId);
            throw new ObjectNotFoundException(String.format("User id=%s not found", userId));
        }
        if (!userStorage.checkIsUserInStorage(friendId)) {
            log.info("User id={} not found", friendId);
            throw new ObjectNotFoundException(String.format("User id=%s not found", friendId));
        }
        ArrayList<User> resultList = new ArrayList<>(userStorage.getListOfFriends(userId));
        resultList.retainAll(userStorage.getListOfFriends(friendId));
        log.info("A shared list of users' friends has been sent id={} и id={}", userId, friendId);
        return resultList;
    }

    public boolean checkIsUserDataCorrect(User newUser) {
        if (newUser.getLogin().contains(" ")) {
            log.info("Invalid specified login");
            throw new ValidationException("Invalid specified login");
        } else if (getInstance(newUser.getBirthday()).isAfter(Instant.now())) {
            log.info("Incorrect date of birth is specified");
            throw new ValidationException("Incorrect date of birth is specified");
        }
        if (newUser.getName().isBlank()) {
            newUser.setName(newUser.getLogin());
        }
        return true;
    }

    private Instant getInstance(String time) {
        return Instant.from(ZonedDateTime.of(LocalDate.parse(time, dateTimeFormatter),
                LocalTime.of(0, 0), ZoneId.of("Europe/Moscow")));
    }
}