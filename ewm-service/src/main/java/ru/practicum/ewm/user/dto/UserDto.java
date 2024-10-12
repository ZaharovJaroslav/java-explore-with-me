package ru.practicum.ewm.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    String email;
    Long id;
    String name;
}
