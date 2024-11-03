package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserShortDto {
    @NotNull
    private Long id;

    @NotNull
    private String name;
}