package ru.practicum.ewm.event.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.dto.CompilationDto;
import ru.practicum.ewm.event.dto.NewCompilationDto;
import ru.practicum.ewm.event.dto.UpdateCompilationRequest;
import ru.practicum.ewm.event.mapper.CompilationMapper;
import ru.practicum.ewm.event.model.Compilation;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.compilation.CompilationRepository;
import ru.practicum.ewm.event.repository.compilation.CompilationSpecification;
import ru.practicum.ewm.event.repository.event.EventRepository;
import ru.practicum.ewm.event.repository.event.EventSpecification;
import ru.practicum.ewm.event.service.event.EventService;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventSpecification eventSpecification;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final CompilationSpecification compilationSpecification;

    @Override
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        log.debug("addCompilation({});", newCompilationDto);
        List<Event> events = new ArrayList<>();
        Set<Event> eventsForCompilation;
        compilationTitleValidator(newCompilationDto.getTitle());
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findAll(eventSpecification.findByEventIdsBuild(newCompilationDto.getEvents()));
            List<Long> eventsIds = events.stream().map(Event::getId).toList();
            eventsForCompilation = new HashSet<>(events);
            Compilation compilation =  CompilationMapper.toCompilation(newCompilationDto,eventsForCompilation);
            compilation.prePersist();
            Compilation newcompilation = compilationRepository.save(compilation);
            events = eventService.setConfirmedRequests(events);
            compilation =  CompilationMapper.toCompilation(newCompilationDto,eventsForCompilation);
            return CompilationMapper.toCompilationDto(newcompilation);
        } else {
            log.info("Создание подборки без событий так как передано 0 событий");
            eventsForCompilation = new HashSet<>(events);
            Compilation compilation = CompilationMapper.toCompilation(newCompilationDto,eventsForCompilation);
            compilation.prePersist();
            return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        }
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.debug("updateCompilation({}, {})", compId, updateCompilationRequest);
        List<Event> events = new ArrayList<>();
        Set<Event> eventsForCompilation;
        Compilation compilation =  compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + compId + "не найден"));
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            events = eventRepository.findAll(eventSpecification.findByEventIdsBuild(updateCompilationRequest.getEvents()));
            eventsForCompilation = new HashSet<>(events);
            compilation.setEvents(eventsForCompilation);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilationTitleValidator(updateCompilationRequest.getTitle());
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.debug("deleteCompilation{})", compId);
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + compId + "не найден"));
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
       Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id = " + compId + "не найден"));
        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> findAll(Boolean pinned, int from, int size) {
        log.debug("findAll({}, {}, {})", pinned, from, size);
        List<Compilation> compilations = new ArrayList<>();
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").ascending());
        compilations = compilationRepository
                .findAll(compilationSpecification.hasPinnedEqual(pinned), pageable).toList();

        return compilations.stream().map(CompilationMapper::toCompilationDto).toList();
    }

    public void compilationTitleValidator(String title) {
        log.debug("validate({})",title);
        if (title == null || title.isBlank()) {
            throw  new ValidationException("Не задан Заголовок события, Переданное значение: " + title);
        }
        if (title.length() > 50 || title.length() < 1) {
            throw  new ValidationException("Не задан Заголовок события, Переданное значение: " + title);
        }
    }
}