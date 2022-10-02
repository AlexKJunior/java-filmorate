INSERT INTO genres (genre_name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Документальный'),
       ('Вестерн'),
       ('Ужасы');

DELETE
FROM genres
WHERE genre_id > 6;
