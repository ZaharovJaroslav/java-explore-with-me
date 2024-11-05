package ru.practicum.ewm.compilation.mapper;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.mapper.EventMapper;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public CompilationDto toDto(Compilation compilation, Long view) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned() != null && compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(compilation.getEvents().stream()
                .map(x -> eventMapper.toShort(x, view))
                .collect(Collectors.toList()));
        return compilationDto;
    }
}
