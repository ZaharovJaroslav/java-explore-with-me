package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.event.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId,
                                 @RequestBody NewEventDto newEventDto) {
        log.info("Добавление нового события. POST /users/userId/events userId={}, newEvent = {}.", userId, newEventDto);
        return eventService.addEvent(userId, newEventDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventUser(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @RequestBody UpdateEventUserRequest request) {
        log.info("Запрос на изменение события добавленного текущим пользователем: {}, {}, {}", userId, eventId, request);
        return eventService.updateEventUser(userId, eventId, request);
    }

    @GetMapping("/{userId}/events")
    public List<EventFullDto> findEventsUser(@PathVariable Long userId,
                                             @RequestParam(value = "from", defaultValue = "0") int from,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Запрос на Получение событий, добавленных текущим пользователем. Переданные фильтры: {}, {}, {}", userId, from, size);
        return eventService.findEventsUser(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findEventsUser(@PathVariable Long userId,
                                             @PathVariable Long eventId) {
        log.info("Запрос на Получение полной информации о событии добавленном текущим пользователем {}, {}", userId, eventId);
        return eventService.findEventUser(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findRequestUser(@PathVariable Long userId,
                                                         @PathVariable Long eventId) {
        log.info("Запрос на Получение информации о запросах на участие в событии текущего пользователя." +
                " Переданные данные: {}, {}", userId, eventId);
        return eventService.findRequestUser(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult moderationRequestsUser(@PathVariable Long userId,
                                                                 @PathVariable Long eventId,
                                                                 @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Запрос на (подтверждение/отменена) заявок на участие в событии текущего пользователя." +
                " Переданные данные: {}, {}, {}", userId,eventId,request);
       return eventService.moderationRequestsUser(userId,eventId, request);
    }
}