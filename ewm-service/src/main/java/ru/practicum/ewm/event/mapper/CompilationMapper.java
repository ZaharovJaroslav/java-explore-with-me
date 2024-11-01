package ru.practicum.ewm.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.dto.CompilationDto;
import ru.practicum.ewm.event.dto.NewCompilationDto;
import ru.practicum.ewm.event.dto.UpdateCompilationRequest;
import ru.practicum.ewm.event.model.Compilation;
import ru.practicum.ewm.event.model.Event;
import java.util.Set;

@Component
public class CompilationMapper {
    public static Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static Compilation toCompilationUpdate(UpdateCompilationRequest updateCompilation, Set<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(updateCompilation.getPinned())
                .title(updateCompilation.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        return  CompilationDto.builder()
                .events(compilation.getEvents().stream().map(EventMapper::toEventShortDto).toList())
                .id(compilation.getId())
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}