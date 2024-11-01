package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.CompilationDto;
import ru.practicum.ewm.event.service.compilation.CompilationService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto findCompilationById(@PathVariable Long compId) {
        log.info("Запрос на получение подборки событий по его id {}", compId);
        return compilationService.findCompilationById(compId);
    }

    @GetMapping
    public List<CompilationDto> findAll(@RequestParam(required = false) Boolean pinned,
                                        @RequestParam(required = false, defaultValue = "0") int from,
                                        @RequestParam(required = false, defaultValue = "10") int size) {
        log.info("Запрос на получение подборок событий. Переданные фильтры: {}, {}, {}", pinned, from, size);
        return compilationService.findAll(pinned,from,size);
    }
}