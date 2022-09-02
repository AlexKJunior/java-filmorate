package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private static UserController userController;
    private static User testUser;

    @BeforeAll
    public static void beforeAll() {
        userController = new UserController();
    }

    @BeforeEach
    public void beforeEach() {
        testUser = new User();
        testUser.setName("Alex");
        testUser.setLogin("TestUser");
        testUser.setBirthday("2011-11-11");
        testUser.setEmail("Alex@alex.ru");
    }

    @Test
    public void shouldApproveUserWithCorrectData() throws ValidationException {
        assertTrue(userController.checkIsUserDataCorrect(testUser),
                "Корректная версия User не прошла проверку");
    }

    @Test
    public void shouldDeclineUserWithIncorrectEmail() {
        testUser.setEmail(null);
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setEmail("");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setEmail(" ");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setEmail("Alex.alex.ru");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
    }

    @Test
    public void shouldDeclineUserWithIncorrectLogin() {
        testUser.setLogin(null);
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setLogin("");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setLogin(" ");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setLogin("Alex ");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
    }

    @Test
    public void shouldDeclineUserWithIncorrectBirthDay() {
        testUser.setBirthday(null);
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
        testUser.setBirthday("2024-11-11");
        assertThrows(ValidationException.class, () -> userController.checkIsUserDataCorrect(testUser));
    }

    @Test
    public void shouldReplaceEmptyNameByLogin() throws ValidationException {
        testUser.setLogin("CorrectLogin");
        testUser.setName("");
        userController.checkIsUserDataCorrect(testUser);
        assertEquals("CorrectLogin", testUser.getName());
    }
}