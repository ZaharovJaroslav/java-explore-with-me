package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.event.dto.EventAdminParam;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUserParam;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.constant.Constants.DATE_FORMAT;


@RestController
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEventPrivate(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.addEventPrivate(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventPrivate(@PathVariable Long userId,
                                               @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        return eventService.getEventPrivate(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdPrivate(@PathVariable Long userId,
                                            @PathVariable Long eventId) {
        return eventService.getEventByIdPrivate(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventPrivate(@PathVariable Long userId, @PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventUserRequest updateEvent) {
        return eventService.updateEventPrivate(userId, eventId, updateEvent);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsUserToEventPrivate(@PathVariable Long userId,
                                                                       @PathVariable Long eventId) {
        return eventService.getRequestsUserToEventPrivate(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateEventRequestStatusPrivate(@PathVariable Long userId,
                                                                          @PathVariable Long eventId,
                                                                          @Valid @RequestBody EventRequestStatusUpdateRequest updateRequests) {
        return eventService.updateEventRequestStatusPrivate(userId, eventId, updateRequests);
    }

    @GetMapping("/admin/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEventsAdmin(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<String> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
                                             @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
                                             @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size) {
        EventAdminParam eventAdminParam = new EventAdminParam();
        eventAdminParam.setUsers(users);
        eventAdminParam.setStates(states);
        eventAdminParam.setCategories(categories);
        eventAdminParam.setRangeStart(rangeStart);
        eventAdminParam.setRangeEnd(rangeEnd);
        eventAdminParam.setFrom(from);
        eventAdminParam.setSize(size);

       return eventService.getEventsAdmin(eventAdminParam);
    }

    @PatchMapping("/admin/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventAdmin(@PathVariable Long eventId, @Valid @RequestBody UpdateEventAdminRequest updateEventAdmin) {
        return eventService.updateEventAdmin(eventId, updateEventAdmin);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsPublic(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {

        EventUserParam eventUserParam = new EventUserParam();
        eventUserParam.setText(text);
        eventUserParam.setCategories(categories);
        eventUserParam.setPaid(paid);
        eventUserParam.setRangeStart(rangeStart);
        eventUserParam.setRangeEnd(rangeEnd);
        eventUserParam.setOnlyAvailable(onlyAvailable);
        eventUserParam.setSort(sort);
        eventUserParam.setFrom(from);
        eventUserParam.setSize(size);

        return eventService.getEventsPublic(eventUserParam, request);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdPublic(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getEventByIdPublic(id, request);
    }
}