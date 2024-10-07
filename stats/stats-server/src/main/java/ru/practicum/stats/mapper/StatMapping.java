package ru.practicum.stats.mapper;

import ru.practicum.dto.StatDto;
import org.springframework.stereotype.Component;
import ru.practicum.stats.model.Stat;

@Component
public class StatMapping {

    public static StatDto toStatDto(Stat stat) {
        return StatDto.builder()
                .app(stat.getApp())
                .uri(stat.getUri())
                .hits(+1L)
                .build();
    }
}

