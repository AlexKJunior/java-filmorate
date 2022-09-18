package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    public final FilmStorage filmStorage;
    public final UserStorage userStorage;
    private final DateTimeFormatter dateTimeFormatter;
    private final static Instant MIN_RELEASE_DATA = Instant.from(ZonedDateTime.of(LocalDateTime.of(1895, 12,
            28, 0, 0), ZoneId.of("Europe/Moscow")));


    @Autowired
    public FilmService(FilmStorage filmStorage, InMemoryUserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int filmId) {
        if (filmStorage.checkIsFilmInStorage(filmId)) {
            return filmStorage.getFilmById(filmId);
        } else {
            log.info("Film id={} Not found", filmId);
            throw new ObjectNotFoundException(String.format("Film id=%s Not found", filmId));
        }
    }

    public Film addFilm(Film newFilm) {
        if (checkIsFilmDataCorrect(newFilm)) {
            return filmStorage.addFilm(newFilm);
        } else {
            return null;
        }
    }

    public Film updateFilm(Film updatedFilm) {
        if (filmStorage.checkIsFilmInStorage(updatedFilm)) {
            if (checkIsFilmDataCorrect(updatedFilm)) {
                return filmStorage.updateFilm(updatedFilm);
            } else {
                return null;
            }
        } else {
            log.info("Film id={} Not found", updatedFilm);
            throw new ObjectNotFoundException(String.format("Failed to update movie data id=%s because " +
                    "film not found", updatedFilm.getId()));
        }
    }

    public void addLike(int filmId, int userId) {
        if (!filmStorage.checkIsFilmInStorage(filmId)) {
            log.info("Film id={} not found", filmId);
            throw new ObjectNotFoundException(String.format("Film id=%s not found", userId));
        }
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("User id={} not found", userId);
            throw new ObjectNotFoundException(String.format("User id=%s not found", userId));
        }
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        if (!filmStorage.checkIsFilmInStorage(filmId)) {
            log.info("Film id={} not found", filmId);
            throw new ObjectNotFoundException(String.format("Film id=%s not found", userId));
        }
        if (!userStorage.checkIsUserInStorage(userId)) {
            log.info("User id={} not found", userId);
            throw new ObjectNotFoundException(String.format("User id=%s not found", userId));
        }
        if (!filmStorage.checkIsFilmHasLikeFromUser(filmId, userId)) {
            log.info("For the movie id={} like from the user id={} not found", filmId, userId);
            throw new ValidationException(String.format("For the movie id=%s  like from the user id=%s not found",
                    filmId, userId));
        }
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Sent a list of {} movies with the most likes", count);
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> (f1.getSetOfLikes().size() - f2.getSetOfLikes().size()) * (-1))
                .limit(count)
                .collect(Collectors.toList());
    }

    public boolean checkIsFilmDataCorrect(Film newFilm) {
        if (getInstance(newFilm.getReleaseDate()).isBefore(MIN_RELEASE_DATA)) {
            log.info("The movie's release date is incorrect");
            throw new ValidationException(String.format("The movie's release date is incorrect. Date required" +
                    " not earlier %s", MIN_RELEASE_DATA));
        } else {
            return true;
        }
    }

    private Instant getInstance(String time) {
        return Instant.from(ZonedDateTime.of(LocalDate.parse(time, dateTimeFormatter),
                LocalTime.of(0, 0), ZoneId.of("Europe/Moscow")));
    }
}