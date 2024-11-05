package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50)
    private String title;

    private Boolean pinned;

    private List<Long> events;
}