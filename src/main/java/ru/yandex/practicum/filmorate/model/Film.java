package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    private static int identificator = 0;
    private int id;
    @NotBlank(message = "The name of the movie is not specified")
    private String name;
    @Size(max = 200, message = "The length of the description should be from 0 to 200 characters")
    private String description;
    @NotNull(message = "No release date specified")
    private String releaseDate;
    @Min(1)
    private long duration;
    private int rate;
    private Set<Integer> setOfLikes;

    public void generateAndSetId() {
        setId(++identificator);
    }

    public void generateSetOfLikes() {
        this.setOfLikes = new HashSet<>();
    }

    public void addLike(int userId) {
        setOfLikes.add(userId);
    }

    public void deleteLike(int userId) {
        setOfLikes.remove(userId);
    }
}