package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService services;

    @Autowired
    public GenreController(GenreService services) {
        this.services = services;
    }


    @GetMapping
    public List<Genre> findAll() {
        log.info("Получен запрос на получение списка жанров");
        return services.getAll();
    }


    @GetMapping("{id}")
    public Genre findById(@PathVariable("id") int genreId) {
        log.info("Получен запрос на получение жанра id={}", genreId);
        return services.getById(genreId);
    }

}