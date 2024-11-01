package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.event.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class EventPublicController {
    private final EventService eventService;

    @GetMapping("/{id}")
    public EventFullDto getById(HttpServletRequest httpServletRequest, @PathVariable Long id) {
        log.info("Запрос подробной информации об опубликованном событии по его идентификатору {}", id);
       return eventService.findByIdPublic(httpServletRequest, id);
    }

    @GetMapping
    public List<EventShortDto> getEvents(HttpServletRequest httpServletRequest,
                                         @RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) String rangeStart,
                                         @RequestParam(required = false) String rangeEnd,
                                         @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Запрос на Получение событий с возможностью фильтрации {}, {}, {}  {}, {}, {} {} {} {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.findEventsPublic(httpServletRequest, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }
}