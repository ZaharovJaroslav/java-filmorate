package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    @NonNull
    private Integer id;
    @NonNull
    private Integer friendId;
    @NonNull
    private boolean isFriend;
}

