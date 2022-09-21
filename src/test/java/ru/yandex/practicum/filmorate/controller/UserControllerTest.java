package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {
    private static final ObjectMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository mockRepository;

    @BeforeEach
    public void setUp() {
        User user = User.builder()
                .id(1L)
                .email("mail@mail.ru")
                .login("dolore")
                .name("Nick Name")
                .birthday(LocalDate.of(1981, 11, 15))
                .build();
        when(mockRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("Method GET /users/1, expected host answer OK")
    public void testFindUsrById_OK_200() throws Exception {
        mockMvc.perform(get("/users/1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("mail@mail.ru")))
                .andExpect(jsonPath("$.login", is("dolore")))
                .andExpect(jsonPath("$.name", is("Nick Name")))
                .andExpect(jsonPath("$.birthday", is("1981-11-15")));
        verify(mockRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Method POST /users, expected host answer CREATED")
    public void testPostNewUser_CREATED_201() throws Exception {
        User newUser = User.builder()
                .id(2L)
                .email("200mail@mail.ru")
                .login("200dolore")
                .name("Second Name")
                .birthday(LocalDate.of(1991, 12, 25))
                .build();
        when(mockRepository.save(any(User.class))).thenReturn(newUser);

        mockMvc.perform(post("/users")
                        .content(jsonMapper.writeValueAsString(newUser))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.email", is("200mail@mail.ru")))
                .andExpect(jsonPath("$.login", is("200dolore")))
                .andExpect(jsonPath("$.name", is("Second Name")))
                .andExpect(jsonPath("$.birthday", is("1991-12-25")));
        verify(mockRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Method GET /users, expected host answer OK")
    public void testFindAllUsers_OK_200() throws Exception {
        List<User> users = Arrays.asList(
                User.builder()
                        .id(1L)
                        .email("mail@mail.ru")
                        .login("dolore")
                        .name("Nick Name")
                        .birthday(LocalDate.of(1981, 11, 15))
                        .build(),
                User.builder()
                        .id(2L)
                        .email("200mail@mail.ru")
                        .login("200dolore")
                        .name("Second Name")
                        .birthday(LocalDate.of(1991, 12, 25))
                        .build());
        when(mockRepository.findAll()).thenReturn(users);
        mockMvc.perform(get("/users"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("mail@mail.ru")))
                .andExpect(jsonPath("$[0].login", is("dolore")))
                .andExpect(jsonPath("$[0].name", is("Nick Name")))
                .andExpect(jsonPath("$[0].birthday", is("1981-11-15")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].email", is("200mail@mail.ru")))
                .andExpect(jsonPath("$[1].login", is("200dolore")))
                .andExpect(jsonPath("$[1].name", is("Second Name")))
                .andExpect(jsonPath("$[1].birthday", is("1991-12-25")));
        verify(mockRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Method PUT /users/1, expected host answer OK")
    public void testUpdateUser_OK_200() throws Exception {
        User updateUser = User.builder()
                .id(1L)
                .email("update@yandex.com")
                .login("UNIT")
                .name("Max Payne")
                .birthday(LocalDate.of(1985, 4, 20))
                .build();
        when(mockRepository.save(any(User.class))).thenReturn(updateUser);

        mockMvc.perform(put("/users/1")
                        .content(jsonMapper.writeValueAsString(updateUser))
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("update@yandex.com")))
                .andExpect(jsonPath("$.login", is("UNIT")))
                .andExpect(jsonPath("$.name", is("Max Payne")))
                .andExpect(jsonPath("$.birthday", is("1985-04-20")));
    }

    @Test
    @DisplayName("Method DELETE /users/1, expected host answer OK")
    public void testDeleteUser_OK_200() throws Exception {
        doNothing().when(mockRepository).deleteById(1L);
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(mockRepository, times(1)).deleteById(1L);
    }
}