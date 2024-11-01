package ru.practicum.ewm.event.service.compilation;

import ru.practicum.ewm.event.dto.CompilationDto;
import ru.practicum.ewm.event.dto.NewCompilationDto;
import ru.practicum.ewm.event.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto request);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request);

    void deleteCompilation(Long compId);

    CompilationDto findCompilationById(Long compId);

    List<CompilationDto> findAll(Boolean pinned,int from, int size);

}

