package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Getter
@Setter
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    private String title;
}
