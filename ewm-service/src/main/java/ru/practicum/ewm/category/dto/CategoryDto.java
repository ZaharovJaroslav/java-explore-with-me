package ru.practicum.ewm.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
@Builder
@AllArgsConstructor
public class CategoryDto {
    private Long id;

    @NotBlank
    @Size(min = 1, max = 50)
    private String name;
}