package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;

    @Email
    @NotBlank
    @Size(min = 6, max = 254, message = "Email length can be 6 to 254")
    private String email;

    @NotBlank
    @Size(min = 2, max = 250, message = "Name can be 2 to 250")
    private String name;
}