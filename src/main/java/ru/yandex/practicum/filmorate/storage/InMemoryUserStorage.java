package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users;

    public InMemoryUserStorage() {
        users = new HashMap<>();
    }

    public User addUser(User newUser) {
        newUser.generateAndSetId();
        newUser.generateSetOfFriends();
        users.put(newUser.getId(), newUser);
        log.info("A new user has been added id={}", newUser.getId());
        return newUser;
    }

    public User updateUser(User updatedUser) {
        updatedUser.setSetOfFriends(users.get(updatedUser.getId()).getSetOfFriends());
        users.put(updatedUser.getId(), updatedUser);
        log.info("User id = {} data updated", updatedUser.getId());
        return updatedUser;
    }

    public List<User> getAllUsers() {
        log.info("Get a list of all users");
        return new ArrayList<>(users.values());
    }

    public User getUserById(int userId) {
        log.info("User received id={}", userId);
        return users.get(userId);
    }

    public void addFriend(int userId, int friendId) {
        users.get(userId).addFriend(friendId);
        log.info("To the user id={} add a new friend id={}", userId, friendId);
        users.get(friendId).addFriend(userId);
        log.info("To the user id={} add a new friend id={}", friendId, userId);
    }

    public void deleteFriend(int userId, int friendId) {
        users.get(userId).deleteFriend(friendId);
        log.info("From the user's friends list id={} friend deleted id={}", userId, friendId);
        users.get(friendId).deleteFriend(userId);
        log.info("From the user's friends list id={} friend deleted id={}", friendId, userId);
    }

    public List<User> getListOfFriends(int userId) {
        log.info("The user's friends list is sent id={}", userId);
        return users.get(userId).getSetOfFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    public boolean checkIsUserInStorage(User user) {
        return users.containsValue(user);
    }

    public boolean checkIsUserInStorage(int userId) {
        return users.containsKey(userId);
    }

    public boolean checkAreTheseUsersFriends(Integer userId, Integer friendId) {
        return (users.get(userId).getSetOfFriends().contains(friendId));
    }
}