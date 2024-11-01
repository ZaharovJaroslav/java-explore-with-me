package ru.practicum.ewm.event.service.event;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto request);

    Event getEventById(Long eventId);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest request);

    EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest request);

    List<EventFullDto> findEventsUser(Long userId, int from, int size);

    EventFullDto findEventUser(Long userId, Long eventId);

    List<EventFullDto> findEventsAdmin(List<Long> users,
                                       List<String> states,
                                       List<Long> categories,
                                       String rangeStart,
                                       String rangeEnd,
                                       int from,
                                       int size);

    List<ParticipationRequestDto> findRequestUser(Long userId, Long eventId);

    EventRequestStatusUpdateResult moderationRequestsUser(Long userId,
                                                          Long eventId,
                                                          EventRequestStatusUpdateRequest request);

    EventFullDto findByIdPublic(HttpServletRequest httpServletRequest, Long id);

    List<EventShortDto> findEventsPublic(HttpServletRequest httpServletRequest,String text,
                                List<Long> categories,
                                Boolean paid,
                                String rangeStart,
                                String rangeEnd,
                                Boolean onlyAvailable,
                                String sort,
                                int from,
                                int size);

    List<Event> setConfirmedRequests(List<Event> events);
}