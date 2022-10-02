INSERT INTO ratings (rating_name)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

DELETE
FROM ratings
WHERE rating_id > 5;