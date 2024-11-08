package ru.practicum.ewm.event.service;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.EventAdminParam;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUserParam;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {

    EventFullDto addEventPrivate(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventPrivate(Long userId, Pageable pageable);

    EventFullDto getEventByIdPrivate(Long userId, Long eventId);

    EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getEventsAdmin(EventAdminParam eventAdminParam);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdmin);

    List<EventShortDto> getEventsPublic(EventUserParam eventUserParam, HttpServletRequest request);

    EventFullDto getEventByIdPublic(Long id, HttpServletRequest request);

    List<ParticipationRequestDto> getRequestsUserToEventPrivate(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestStatusPrivate(Long userId, Long eventId,
                                                                   EventRequestStatusUpdateRequest updateRequests);

    List<EventFullDto> findEventsByUser(Long userId, Long authorId, Pageable pageable);

    List<EventShortDto> findEventsByAllUsers(Long userId, Pageable pageable);

}
