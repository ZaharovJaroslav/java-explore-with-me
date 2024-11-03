package ru.practicum.ewm.compilation.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
public class CompilationDto {
    private Long id;

    private List<EventShortDto> events;

    private Boolean pinned;

    @NotBlank
    private String title;
}