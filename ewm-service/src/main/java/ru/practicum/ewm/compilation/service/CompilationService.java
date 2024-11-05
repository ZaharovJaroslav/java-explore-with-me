package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilationAdmin(NewCompilationDto newCompilationDto);

    void deleteCompilationAdmin(Long compilationId);

    CompilationDto updateCompilationAdmin(Long compilationId, UpdateCompilationRequest updateCompilationRequest);

    List<CompilationDto> findCompilationsPublic(Boolean pinned, Pageable pageable);

    CompilationDto findCompilationPublic(Long compilationId);
}
