package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.event.EventService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor
public class EventAdminController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable Long eventId,
                                        @RequestBody UpdateEventAdminRequest request) {
        log.info("Запрос Администратора на редактирование данных события: {}, {}", eventId, request);
        return eventService.updateEventAdmin(eventId, request);
    }

    @GetMapping
    public List<EventFullDto> findEventsAdmin(@RequestParam(value = "users", required = false) List<Long> users,
                                              @RequestParam(value = "states", required = false) List<String> states,
                                              @RequestParam(value = "categories", required = false) List<Long> categories,
                                              @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                              @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                              @RequestParam(value = "from", defaultValue = "0") Integer from,
                                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Запрос на получение событий по фильтрам: {}, {}, {}, {}, {}, {}, {}",
                 users, states, categories, rangeStart, rangeEnd, from, size);

      return eventService.findEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}