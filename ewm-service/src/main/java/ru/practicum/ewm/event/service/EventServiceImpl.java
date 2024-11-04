package ru.practicum.ewm.event.service;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ParamHitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventAdminParam;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUserParam;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.InvalidRequestException;
import ru.practicum.ewm.exception.ObjectNotFoundException;
import ru.practicum.ewm.exception.RulesViolationException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.constant.Constants.DATE_FORMAT;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final StatsClient client;

    @Override
    @Transactional
    public EventFullDto addEventPrivate(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidRequestException("Field: eventDate. " + "Error: Date must be after than now. Value:"
                    + newEventDto.getEventDate());
        }
        User user = getUserOrThrow(userId);
        Category category = getCategoryOrThrow(newEventDto.getCategory());
        Event event = eventMapper.toEvent(newEventDto, category, user, null);
        event = eventRepository.save(event);

        log.info("Add new event");
        return eventMapper.toFull(event, getHitsEvent(event.getId(),
                LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), false));
    }

    @Override
    public List<EventShortDto> getEventPrivate(Long userId, Pageable pageable) {
        log.info("Get owner events");
        return eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(e -> eventMapper.toShort(e, getHitsEvent(e.getId(),
                        LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), false)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPrivate(Long userId, Long eventId) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);
        log.info("Get information about event for owner");
        return eventMapper.toFull(event, getHitsEvent(event.getId(),
                LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), false));
    }

    @Override
    public EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        getEventOrThrow(eventId);
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new RulesViolationException("Only pending or canceled events can be changed");
        }
        if (updateEventUserRequest.getEventDate() != null) {
            if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new InvalidRequestException("Field: eventDate. " + "Error: Date must be after than now. Value:"
                        + event.getEventDate());
            } else {
                event.setEventDate(updateEventUserRequest.getEventDate());
            }
        }
        if (updateEventUserRequest.getCategory() != null) {
            Long categoryId = updateEventUserRequest.getCategory();
            Category category = getCategoryOrThrow(categoryId);
            event.setCategory(category);
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getLocation() != null) {
            if (updateEventUserRequest.getLocation().getLat() != null) {
                event.setLat(updateEventUserRequest.getLocation().getLat());
            }
            if (updateEventUserRequest.getLocation().getLon() != null) {
                event.setLon(updateEventUserRequest.getLocation().getLon());
            }
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals("SEND_TO_REVIEW")) {
                event.setState(EventState.PENDING);
            } else if (updateEventUserRequest.getStateAction().equals("CANCEL_REVIEW")) {
                event.setState(EventState.CANCELED);
            } else {
                throw new InvalidRequestException("Unknown state, it must be: SEND_TO_REVIEW or CANCEL_REVIEW");
            }
        }
        log.info("Update Event");
        return eventMapper.toFull(eventRepository.save(event), 0L);
    }

    @Override
    public List<EventFullDto> getEventsAdmin(EventAdminParam eventAdminParam) {
        Pageable pageable = PageRequest.of(eventAdminParam.getFrom(),
                eventAdminParam.getSize(), Sort.by("id").ascending());
        if (eventAdminParam.getUsers() == null &&
                eventAdminParam.getStates() == null &&
                eventAdminParam.getCategories() == null &&
                eventAdminParam.getRangeStart() == null &&
                eventAdminParam.getRangeEnd() == null) {
            return eventRepository.findAll(pageable).stream().map(eventMapper::toEmpty).collect(Collectors.toList());
        }

        List<Event> events = eventRepository.findEventsByParams(eventAdminParam.getUsers(), eventAdminParam.getStates(),
                eventAdminParam.getCategories(), eventAdminParam.getRangeStart(),
                eventAdminParam.getRangeEnd(), pageable);


        return events.stream().map(e -> eventMapper.toFull(e, getHitsEvent(e.getId(),
                        LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), false)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdmin) {
        Event event = getEventOrThrow(eventId);

        if (updateEventAdmin.getStateAction() != null) {
            if (updateEventAdmin.getStateAction().equals("PUBLISH_EVENT")) {
                if (!String.valueOf(event.getState()).equals("PENDING")) {
                    throw new RulesViolationException(
                            String.format("Event have state=%s but must have state PENDING", event.getState()));
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEventAdmin.getStateAction().equals("REJECT_EVENT")) {
                if (String.valueOf(event.getState()).equals("PUBLISHED")) {
                    throw new RulesViolationException(
                            String.format("Event have state=%s adn can't be REJECT", event.getState()));
                }
                event.setState(EventState.CANCELED);
            } else {
                throw new RulesViolationException("StateAction must be PUBLISH_EVENT or REJECT_EVENT");
            }
        }

        if (updateEventAdmin.getAnnotation() != null) {
            event.setAnnotation(updateEventAdmin.getAnnotation());
        }
        if (updateEventAdmin.getCategory() != null && updateEventAdmin.getCategory() != 0) {
            Category category = getCategoryOrThrow(updateEventAdmin.getCategory());
            event.setCategory(category);
        }
        if (updateEventAdmin.getDescription() != null) {
            event.setDescription(updateEventAdmin.getDescription());
        }

        if (updateEventAdmin.getEventDate() != null) {
            if (updateEventAdmin.getEventDate().isBefore(LocalDateTime.now().minusHours(1))) {
                throw new InvalidRequestException("EventDate must be 1 hour earlier then time of publication");
            }
            event.setEventDate(updateEventAdmin.getEventDate());
        }
        if (updateEventAdmin.getLocation() != null) {
            event.setLon(updateEventAdmin.getLocation().getLon());
            event.setLat(updateEventAdmin.getLocation().getLat());
        }
        if (updateEventAdmin.getPaid() != null) {
            event.setPaid(updateEventAdmin.getPaid());
        }
        if (updateEventAdmin.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdmin.getParticipantLimit());
        }
        if (updateEventAdmin.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdmin.getRequestModeration());
        }
        if (updateEventAdmin.getTitle() != null) {
            event.setTitle(updateEventAdmin.getTitle());
        }
        event.setId(eventId);
        eventRepository.save(event);
        return eventMapper.toFull(event, getHitsEvent(eventId,
                LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), false));
    }

    @Override
    public List<EventShortDto> getEventsPublic(EventUserParam eventUserParam, HttpServletRequest request) {
        ParamHitDto paramHitDto = new ParamHitDto();
        paramHitDto.setIp(request.getRemoteAddr());
        paramHitDto.setUri(request.getRequestURI());
        paramHitDto.setApp("ewm-main-service");
        paramHitDto.setTimestamp(LocalDateTime.now());
        client.save(paramHitDto);

        Specification<Event> specification = (((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (eventUserParam.getText() != null) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("annotation")), "%" +
                                        eventUserParam.getText().toLowerCase() + "%"),
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("description")), "%" +
                                        eventUserParam.getText().toLowerCase() + "%")));
            }
            if (eventUserParam.getPaid() != null) {
                predicates.add(criteriaBuilder.equal(root.get("paid"), eventUserParam.getPaid()));
            }

            if (eventUserParam.getRangeStart() == null && eventUserParam.getRangeEnd() == null) {
                predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), LocalDateTime.now()));
            } else {

                if (eventUserParam.getRangeStart() != null &&
                        eventUserParam.getRangeEnd() != null &&
                        eventUserParam.getRangeStart().isAfter(eventUserParam.getRangeEnd())) {
                    throw new InvalidRequestException("rangeStart can't be after rangeEnd");
                } else if (eventUserParam.getRangeStart() != null) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), eventUserParam.getRangeStart()));
                } else {
                    predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), eventUserParam.getRangeEnd()));
                }
            }

            if (eventUserParam.getOnlyAvailable() != null) {
                predicates.add(criteriaBuilder.lessThan(root.get("confirmedRequests"), root.get("participantLimit")));
            }
            predicates.add(criteriaBuilder.equal(root.get("state"), EventState.PUBLISHED));

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        }));

        if (eventUserParam.getSort() == null) {
            Pageable pageable = PageRequest.of(eventUserParam.getFrom() / eventUserParam.getSize(),
                    eventUserParam.getSize(), Sort.by("id"));
            return getOutputEventsStream(specification, pageable);

        } else if (eventUserParam.getSort().equals(String.valueOf(EventSort.EVENT_DATE))) {
            Pageable pageable = PageRequest.of(eventUserParam.getFrom() / eventUserParam.getSize(),
                    eventUserParam.getSize(), Sort.by("eventDate"));
            return getOutputEventsStream(specification, pageable);

        } else if (eventUserParam.getSort().equals(String.valueOf(EventSort.VIEWS))) {
            Pageable pageable = PageRequest.of(eventUserParam.getFrom() / eventUserParam.getSize(),
                    eventUserParam.getSize(), Sort.unsorted());

            return getOutputEventsStream(specification, pageable).stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }
        throw new InvalidRequestException("Sort cat be EVENT_DATE or VIEWS");
    }

    public List<EventShortDto> getOutputEventsStream(Specification<Event> specification, Pageable pageable) {
        List<Event> allEvents = eventRepository.findAll(specification, pageable).getContent();
        return allEvents.stream()
                .map(r -> eventMapper.toShort(r, getHitsEvent(r.getId(),
                        LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), false)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdPublic(Long id, HttpServletRequest request) {
        ParamHitDto statsDto = new ParamHitDto();
        statsDto.setIp(request.getRemoteAddr());
        statsDto.setUri(request.getRequestURI());
        statsDto.setApp("ewm-main-service");
        statsDto.setTimestamp(LocalDateTime.now());
        client.save(statsDto);

        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Event with id=%d was not found", id)));

        Long view = getHitsEvent(id, LocalDateTime.now().minusDays(100).format(DateTimeFormatter.ofPattern(DATE_FORMAT)),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)), true);

        return eventMapper.toFull(event, view);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsUserToEventPrivate(Long userId, Long eventId) {
        getEventOrThrow(eventId);
        getUserOrThrow(userId);
        log.info("All requests to event");
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestStatusPrivate(Long userId, Long eventId,
                                                                          EventRequestStatusUpdateRequest updateRequest) {
            EventRequestStatusUpdateResult updateResult;
            List<Request> confirmedRequests = new ArrayList<>();
            List<Request> rejectedRequests = new ArrayList<>();
            int countRequests = updateRequest.getRequestIds().size();
            List<Request> requests = requestRepository.findByIdIn(updateRequest.getRequestIds());
            getUserOrThrow(userId);
            Event event = getEventOrThrow(eventId);

            if (!event.getInitiator().getId().equals(userId)) {
                throw new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId));
            }
            for (Request request : requests) {
                if (!request.getStatus().equals(RequestStatus.PENDING)) {
                    throw new RulesViolationException("Request status is not PENDING");
                }
            }
            if (updateRequest.getStatus() != null) {
                switch (updateRequest.getStatus()) {
                    case "CONFIRMED":
                        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()
                                || event.getParticipantLimit() > event.getConfirmedRequests() + countRequests) {
                            requests.forEach(request -> request.setStatus(RequestStatus.CONFIRMED));
                            event.setConfirmedRequests(event.getConfirmedRequests() + countRequests);
                            confirmedRequests.addAll(requests);

                        } else if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
                            throw new RulesViolationException("Participant Limit");
                        } else {
                            for (Request request : requests) {
                                if (event.getParticipantLimit() > event.getConfirmedRequests()) {
                                    request.setStatus(RequestStatus.CONFIRMED);
                                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                                    confirmedRequests.add(request);
                                } else {
                                    request.setStatus(RequestStatus.REJECTED);
                                    rejectedRequests.add(request);
                                }
                            }
                        }
                        break;
                    case "REJECTED":
                        requests.forEach(request -> request.setStatus(RequestStatus.REJECTED));
                        rejectedRequests.addAll(requests);
                }
            }
            eventRepository.save(event);
            requestRepository.saveAll(requests);

            List<ParticipationRequestDto> confirmed = confirmedRequests.stream()
                    .map(requestMapper::toDto)
                    .collect(Collectors.toList());
            List<ParticipationRequestDto> rejected = rejectedRequests.stream()
                    .map(requestMapper::toDto)
                    .collect(Collectors.toList());
            updateResult = new EventRequestStatusUpdateResult(confirmed, rejected);
            log.info(String.format("Update states (%s) of event's (id=%d) requests.", updateRequest.getStatus(),
                                                                                      eventId));
            return updateResult;
    }

    private Category getCategoryOrThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new ObjectNotFoundException(
                        String.format("Category with id=%d was not found", categoryId)));
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Event with id=%d was not found", eventId)));
    }

    private User getUserOrThrow(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("User with id=%d not found", userId)));
    }

    private Long getHitsEvent(Long eventId, String start, String end, Boolean unique) {

        List<String> uris = new ArrayList<>();
        uris.add("/events/" + eventId);

        List<StatDto> output = client.getStats(start, end, uris, unique);

        Long view = 0L;

        if (!output.isEmpty()) {
            view = output.get(0).getHits();
        }
        return view;
    }
}
