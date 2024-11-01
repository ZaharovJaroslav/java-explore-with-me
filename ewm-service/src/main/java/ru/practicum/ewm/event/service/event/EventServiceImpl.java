package ru.practicum.ewm.event.service.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.ParamHitDto;
import ru.practicum.dto.StatDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.dto.EventFilterPublic;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.mapper.DataTimeMapper;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.event.mapper.UpdateEventRequestMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.ParticipationRequest;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.model.StateAction;
import ru.practicum.ewm.event.repository.ParticipationRequest.ParticipationRequestRepository;
import ru.practicum.ewm.event.repository.ParticipationRequest.ParticipationRequestSpecification;
import ru.practicum.ewm.event.repository.event.EventRepository;
import ru.practicum.ewm.event.repository.event.EventSpecification;
import ru.practicum.ewm.event.validator.CreateEventValidator;
import ru.practicum.ewm.event.validator.UpdateEventValidator;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.user.service.UserService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventSpecification eventSpecifications;
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestSpecification participationRequestSpecification;
    private final StatsClient statsClient;
    private static final String nameApp = "ewmService";

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        log.debug("addEvent({}, {})", userId, newEventDto);
        CreateEventValidator validator = new CreateEventValidator(newEventDto);
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Запрос составлен некорректно", validator.getMessages());
        }
        int timeLimitInSeconds = 7200;
        User user = userService.getUserById(userId);
        Category category = getCategoryById(newEventDto.getCategory());

        Event event = EventMapper.toEvent(user, category, newEventDto);
        event.prePersist();
        event.setState(State.PENDING);
        checkTimeLimitEventDate(event.getEventDate(), timeLimitInSeconds);
        return EventMapper.toEventFullDtoWhenCreated(eventRepository.save(event));
    }

    private Category getCategoryById(Long categoryId) {
        log.info("Запрос на получение категории из Базы данных по id = {}", categoryId);
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id = " + categoryId + "не найден"));
    }

    public Event getEventById(Long eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NotFoundException("Категория с id = " + eventOptional + "не найдена");
        }

        return eventOptional.get();
    }

    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEvent) {
        log.debug("updateEventAdmin({}, {})",eventId, updateEvent);
        int timeLimitInSeconds = 3600;
        Event  eventToUpdate = getEventById(eventId);
        UpdateEventValidator validator =
              new UpdateEventValidator(UpdateEventRequestMapper.fromAdmintoUpdateEventRequest(updateEvent));
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Запрос составлен некорректно", validator.getMessages());
        }
        checkStateAction(eventToUpdate, updateEvent);
        checkTimeLimitEventDate(eventToUpdate.getEventDate(), timeLimitInSeconds);
        Event updatingResults =  updateEventsFieldsByAdmin(eventToUpdate, updateEvent);
        eventRepository.save(updatingResults);
        return EventMapper.toEventFullDtoWhenCreated(updatingResults);
    }

    private void checkStateAction(Event oldEvent, UpdateEventAdminRequest newEvent) {

        if (newEvent.getStateAction() == StateAction.PUBLISH_EVENT) {
            if (oldEvent.getState() != State.PENDING) {
                throw new ConflictException("Невозможно опубликовать событие, поскольку его можно " +
                        "публиковать, только если оно в состоянии ожидания публикации.");
            }
        }
        if (newEvent.getStateAction() == StateAction.REJECT_EVENT) {
            if (oldEvent.getState() == State.PUBLISHED) {
                throw new ConflictException("Событие опубликовано, поэтому отменить его невозможно.");
            }
        }
        if (oldEvent.getState().equals(State.CANCELED)
                && newEvent.getStateAction().equals(StateAction.PUBLISH_EVENT)) {
            throw new ConflictException("Невозможно отменить опубликованное событие.");
        }
    }

    private void checkTimeLimitEventDate(Instant eventDate, int timeLimitInSeconds) {
        log.info("Запрос на валидацию даты начала изменяемого события");
        if (eventDate.isBefore(Instant.now().plusSeconds(timeLimitInSeconds))) {
            throw new ValidationException("Дата начала изменяемого события должна быть не ранее чем за" +
                                          timeLimitInSeconds + " секунд от даты публикации");
        }
    }

    private Event updateEventsFieldsByAdmin(Event oldEvent, UpdateEventAdminRequest updateEvent) {
        int timeLimitInSeconds = 3600;
        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            oldEvent.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getCategory() != null) {
            oldEvent.getCategory().setId(updateEvent.getCategory());
        }
        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isBlank()) {
            oldEvent.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getEventDate() != null) {
            checkTimeLimitEventDate(DataTimeMapper.toInstant(updateEvent.getEventDate()), timeLimitInSeconds);
            oldEvent.setEventDate(DataTimeMapper.toInstant(updateEvent.getEventDate()));
        }
        if (updateEvent.getLocation() != null) {
            oldEvent.setLocation((updateEvent.getLocation()));
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (StateAction.CANCEL_REVIEW.equals(updateEvent.getStateAction()) ||
                StateAction.REJECT_EVENT.equals(updateEvent.getStateAction())) {
            oldEvent.setState(State.CANCELED);
        }
        if (StateAction.SEND_TO_REVIEW.equals(updateEvent.getStateAction())) {
            oldEvent.setState(State.PENDING);
        }
        if (StateAction.PUBLISH_EVENT.equals(updateEvent.getStateAction())) {
            oldEvent.setState(State.PUBLISHED);
            oldEvent.setPublishedOn(Instant.now());
        }
        if (updateEvent.getTitle() != null && !updateEvent.getTitle().isBlank()) {
            oldEvent.setTitle(updateEvent.getTitle());
        }
        return oldEvent;
    }

    @Override
    public List<EventFullDto> findEventsAdmin(List<Long> users,
                                              List<String> states,
                                              List<Long> categories,
                                              String rangeStart,
                                              String rangeEnd,
                                              int from,
                                              int size) {
        log.debug("findEventsAdmin({}, {}, {}, {}, {}, {}, {})",
                  users, states, categories, rangeStart, rangeEnd, from, size);
        List<State> stateList = new ArrayList<>();
        Pageable pageable = PageRequest.of(from, size, Sort.by("id").ascending());

        if (users == null && states == null && categories == null && rangeStart == null && rangeEnd == null) {
           return eventRepository.findAll(pageable).stream().map(EventMapper::toEventFullDto).toList();
        }
        if (states != null) {
            stateList = states.stream().map(State::valueOf).toList();
        }
        EventFilter eventFilter = EventFilter.builder()
                .userIds(users)
                .states(stateList)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();

        Specification<Event> specification = eventSpecifications.findByEventFilterBuild(eventFilter);
        List<Event> events = eventRepository.findAll(specification, pageable).toList();
        if (!events.isEmpty()) {
        events = setConfirmedRequests(events);
        Map<Long,String> uris = new HashMap<>();
             for (Event ev : events) {
                 uris.put(ev.getId(), "/events" + ev.getId());
             }
             events = setViews(events, uris);
            return events.stream().map(EventMapper::toEventFullDto).toList();
        } else  {
            List<EventFullDto> evreslt = new ArrayList<>();
            return evreslt;
        }
    }

    public List<Event> setConfirmedRequests(List<Event> events) {
        List<Long> eventsIds = events.stream().map(Event::getId).toList();
        List<ParticipationRequest> participationRequests = participationRequestRepository
                .findAll(participationRequestSpecification
                        .findByEventIdAndStatePublishedBuild(eventsIds, State.CONFIRMED.toString()));

        Map<Long, Long> requests = participationRequests.stream()
                .filter(participationRequest ->  eventsIds.contains(participationRequest.getId()))
                .collect(Collectors.toMap(
                        ParticipationRequest::getId,
                        participationRequest -> participationRequest.getEvent().getId()));
        for (Long id : requests.values()) {
            for (Event ev : events) {
                if (ev.getId() == id) {
                    ev.setConfirmedRequests(+1L);
                }
            }
        }
        return events;
    }

    @Override
    public EventFullDto updateEventUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        log.debug("updateEventUser({}, {}, {})", userId, eventId,updateEvent);
        int timeLimitInSeconds = 7200;
        User user = userService.getUserById(userId);
        Event eventToUpdate = getEventById(eventId);
        if (!eventToUpdate.getInitiator().getId().equals(user.getId())) {
                   throw new ForbiddenException("Пользователь с id: " + user.getId() +
                           "не является создателем события с id: " + eventToUpdate.getInitiator().getId());
        }
        if (eventToUpdate.getState() == State.PUBLISHED) {
            throw new ConflictException("Владельцу события нельзя изменять событие после публикации");
        }
        UpdateEventValidator validator = new UpdateEventValidator(UpdateEventRequestMapper
                .fromUsertoUpdateEventRequest(updateEvent));
        validator.validate();
        if (!validator.isValid()) {
            throw new ValidationException("Запрос составлен некорректно", validator.getMessages());
        }

        Event updatingResults = updateEventsFieldsByUser(eventToUpdate, updateEvent);
        Event savedEvent = eventRepository.save(updatingResults);
        List<Event> events = List.of(savedEvent);
        log.info("Выполнено обновление события с ID = {}.", eventId);
        return EventMapper.toEventFullDtoWhenCreated(updatingResults);
        }

    private Event updateEventsFieldsByUser(Event oldEvent, UpdateEventUserRequest updateEvent) {
        int timeLimitInSeconds = 7200;
        if (updateEvent.getCategory() != null) {
            oldEvent.getCategory().setId(updateEvent.getCategory());
        }
        if (updateEvent.getAnnotation() != null && !updateEvent.getAnnotation().isBlank()) {
            oldEvent.setAnnotation(updateEvent.getAnnotation());
        }
        if (updateEvent.getEventDate() != null) {
            checkTimeLimitEventDate(DataTimeMapper.toInstant(updateEvent.getEventDate()),timeLimitInSeconds);
            oldEvent.setEventDate(DataTimeMapper.toInstant(updateEvent.getEventDate()));
        }
        if (updateEvent.getLocation() != null) {
            oldEvent.setLocation(updateEvent.getLocation());
        }
        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isBlank()) {
            oldEvent.setDescription(updateEvent.getDescription());
        }
        if (updateEvent.getRequestModeration() != null) {
            oldEvent.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getPaid() != null) {
            oldEvent.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (StateAction.CANCEL_REVIEW.equals(updateEvent.getStateAction())) {
            oldEvent.setState(State.CANCELED);
        }
        if (StateAction.SEND_TO_REVIEW.equals(updateEvent.getStateAction())) {
            oldEvent.setState(State.PENDING);
        }
        if (StateAction.PUBLISH_EVENT.equals(updateEvent.getStateAction())) {
            oldEvent.setState(State.PUBLISHED);
            oldEvent.setPublishedOn(Instant.now());
        }
        if (updateEvent.getTitle() != null && !updateEvent.getTitle().isBlank()) {
            oldEvent.setTitle(updateEvent.getTitle());
        }
        return oldEvent;
    }

    @Override
    public List<EventFullDto> findEventsUser(Long userId, int from, int size) {
        log.debug("findEventsUser({}, {}, {}})", userId, from, size);
        Pageable pageable =  PageRequest.of(from, size, Sort.by("id").ascending());
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + "не найден"));
        List<Event> events  = eventRepository
                .findAll(eventSpecifications.findByUserIdBuild(userId), pageable).toList();
        return events.stream().map(EventMapper::toEventFullDtoWhenCreated).collect(Collectors.toList());
    }

    @Override
    public EventFullDto findEventUser(Long userId, Long eventId) {
        log.debug("findEventUser({}, {})", userId,eventId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + "не найден"));
        Event oldEvent;
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isEmpty()) {
                    throw new NotFoundException("Категория с id = " + event + "не найдена");
        }
        Event result = event.get();
        if (!result.getInitiator().getId().equals(userId)) {
                 throw new ForbiddenException("Пользователь с id: " + userId +
                            "не является создателем события с id: " + result.getInitiator().getId());
        }
        return EventMapper.toEventFullDtoWhenCreated(result);
    }

    @Override
    public List<ParticipationRequestDto> findRequestUser(Long userId, Long eventId) {
        log.debug("findRequestUser({}, {})", userId, eventId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + "не найден"));
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NotFoundException("Категория с id = " + eventOptional + "не найдена");
        }
        List<ParticipationRequest> requests = participationRequestRepository
                .findAll(participationRequestSpecification.findAllByEventIdBuild(eventId));

        return requests.stream().map(ParticipationRequestMapper::toParticipationRequestDto).toList();
    }

    public void checkUserExistence(Long userId) {
        log.info("Запрос на проверку наличия пользователя в Базе данных");
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + "не найден"));
    }

    @Override
    public EventRequestStatusUpdateResult moderationRequestsUser(Long userId, Long eventId,
                                                                 EventRequestStatusUpdateRequest request) {
        log.debug("moderationRequestsUser({}, {}, {}", userId, eventId, request);
        EventRequestStatusUpdateResult result;
        checkUserExistence(userId);
        Event event = getEventById(eventId);
        List<ParticipationRequest> participationRequests =
                participationRequestRepository.findAll(participationRequestSpecification
                        .findAllByEventIdBuild(eventId));

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
               log.info("Модерация заявок не требуется так как событие не требует потверждение заявок," +
                    " а так же лимит на участие не ограничен");
               return new EventRequestStatusUpdateResult(new ArrayList<>(),
                                                         new ArrayList<>());
        }
        if (request.getStatus() == State.CONFIRMED) {
            result = confirmedRequest(participationRequests, request, event);
        } else
            result  = canceledRequest(participationRequests, request);

        return result;
    }

    private EventRequestStatusUpdateResult confirmedRequest(List<ParticipationRequest> participationRequests,
                                                           EventRequestStatusUpdateRequest request, Event event) {
        log.info("Запрос на Потверждение запросов");
        long availableLimit;
        List<ParticipationRequest> confirmedRequests = participationRequests.stream()
                .filter(participationRequest -> participationRequest.getStatusRequest() == State.CONFIRMED)
                .collect(Collectors.toList());

        event.setConfirmedRequests((long)confirmedRequests.size());
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит учатников на событие превышен");
        }
        availableLimit = event.getParticipantLimit() - event.getConfirmedRequests();
        confirmedRequests =  participationRequests.stream()
                .filter(participationRequest -> request.getRequestIds().contains(participationRequest.getId()))
                .filter(participationRequest -> participationRequest.getStatusRequest() == State.PENDING)
                .limit(availableLimit)
                .collect(Collectors.toList());
        confirmedRequests.forEach(participationRequest -> participationRequest.setStatusRequest(State.CONFIRMED));
        participationRequestRepository.saveAll(confirmedRequests);
        event.setConfirmedRequests((long)confirmedRequests.size());
        eventRepository.save(event);

        List<ParticipationRequest> participationRequestsCanceled = participationRequestRepository.findAll(
                participationRequestSpecification.findAllCanceledAndPendingBuild(event.getId(),
                        State.REJECTED.toString(),
                        State.PENDING.toString()));

        participationRequestsCanceled.forEach(
                participationRequest -> participationRequest.setStatusRequest(State.REJECTED));
        participationRequestRepository.saveAll(participationRequestsCanceled);

        List<ParticipationRequest> participationRequestsActual =  participationRequestRepository.findAll();

        List<ParticipationRequestDto>  confirmed = participationRequestsActual.stream()
                .filter(participationRequest -> participationRequest.getStatusRequest() == State.CONFIRMED)
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .toList();

        List<ParticipationRequestDto>  cancelled =  participationRequestsActual.stream()
                .filter(participationRequest -> participationRequest.getStatusRequest() == State.REJECTED)
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .toList();

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(cancelled)
                .build();
    }

    private EventRequestStatusUpdateResult canceledRequest(List<ParticipationRequest> participationRequests,
                                                           EventRequestStatusUpdateRequest request) {
        log.info("Запрос на отклонение запросов на участие в событии");

        List<ParticipationRequest> canceledRequests =  participationRequests.stream()
                .filter(participationRequest -> request.getRequestIds().contains(participationRequest.getId()))
                .collect(Collectors.toList());

        for (ParticipationRequest pr : canceledRequests) {
            if (pr.getStatusRequest() == State.CONFIRMED) {
                throw new ConflictException("Нельзя отменить уже принятую заявку на участие в событии");
            }
        }
        canceledRequests.forEach(participationRequest -> participationRequest.setStatusRequest(State.REJECTED));
        participationRequestRepository.saveAll(canceledRequests);


        List<ParticipationRequest> participationRequestsActual =  participationRequestRepository.findAll();

        List<ParticipationRequestDto>  confirmed = participationRequestsActual.stream()
                .filter(participationRequest -> participationRequest.getStatusRequest() == State.CONFIRMED)
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .toList();

        List<ParticipationRequestDto>  cancelled =  participationRequestsActual.stream()
                .filter(participationRequest -> participationRequest.getStatusRequest() == State.REJECTED)
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .toList();

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(cancelled)
                .build();
    }

    @Override
    public EventFullDto findByIdPublic(HttpServletRequest httpServletRequest, Long id) {
        log.debug("findById({})", id);
        List<Event> events =  setConfirmedRequests(List.of(getEventById(id)));
        Event event = events.get(0);
        if (event.getState() != State.PUBLISHED) {
            throw new NotFoundException("Событие не найдено");
        }
        Instant currentTime = Instant.now();
        List<StatDto> stat = statsClient.getStats(DataTimeMapper.instatToString(event.getPublishedOn()),
                DataTimeMapper.instatToString(currentTime),
                List.of("/events" + id),
                true);
        if (!stat.isEmpty()) {
            Long hit = stat.get(0).getHits();
            event.setViews(hit);
        }
        ParamHitDto newStat = new ParamHitDto(nameApp,
                                          "/events" + id,
                                              httpServletRequest.getRemoteAddr(),
                                              DataTimeMapper.instatToString(currentTime));
        statsClient.save(newStat);
        return EventMapper.toEventFullDto(event);
    }

    private Map<Long,String> saveStats(HttpServletRequest httpServletRequest, List<Event> events) {
        Instant currentTime = Instant.now();
        Map<Long,String> uris = new HashMap<>();

        for (Event ev : events) {
            ParamHitDto newStats = new ParamHitDto(nameApp,
                                               "/events" + ev.getId(),
                                                   httpServletRequest.getRemoteAddr(),
                                                   DataTimeMapper.instatToString(currentTime));
            uris.put(ev.getId(), newStats.getUri());
            statsClient.save(newStats);
        }
        return uris;
    }

    private List<Event> setViews(List<Event> events, Map<Long,String> uris) {
        TreeSet<Event> sortedByPublishedOn = new TreeSet<>(Comparator.comparing(Event::getPublishedOn));
        sortedByPublishedOn.addAll(events);
        Instant startTime = sortedByPublishedOn.getFirst().getPublishedOn();
        Instant endTime = sortedByPublishedOn.getLast().getPublishedOn();

        List<StatDto> stat = statsClient.getStats(DataTimeMapper.instatToString(startTime),
                DataTimeMapper.instatToString(endTime),
                uris.values().stream().toList(),
                false);

        if (stat.isEmpty()) {
            events.forEach(event -> event.setViews(0L));
        }
        for (Event ev : events) {
            if (uris.containsKey(ev.getId())) {
                String uri =  uris.get(ev.getId());
                for (StatDto st : stat) {
                    if (st.getUri().equals(uri)) {
                        ev.setViews(st.getHits());
                    } else
                        ev.setViews(0L);
                }
            }
        }
        return events;

    }

    @Override
    public List<EventShortDto> findEventsPublic(HttpServletRequest httpServletRequest,
                                                String text,
                                                List<Long> categories,
                                                Boolean paid,
                                                String rangeStart,
                                                String rangeEnd,
                                                Boolean onlyAvailable,
                                                String sort,
                                                int from,
                                                int size) {
        log.debug("findEventsPublic({}, {}, {}  {}, {}, {} {} {} {})",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        String sortType = "id";
        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                sortType = "eventDate";
            } else
                sortType = "views";
        }
        if (rangeStart != null  && rangeEnd != null) {
           if (DataTimeMapper.toInstant(rangeEnd).isBefore(DataTimeMapper.toInstant(rangeStart))) {
               throw new ValidationException("Временной диапазн фильтрации задан не правильно");
           }
        }

        Pageable pageable = PageRequest.of(from, size, Sort.by(sortType).ascending());
        EventFilterPublic eventFilterPublic = EventFilterPublic.builder()
                .state(State.PUBLISHED)
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .build();

        Page<Event> specification =
                eventRepository.findAll(eventSpecifications.findByEventFilterPublicBuild(eventFilterPublic),pageable);
        List<Event> events = specification.stream().toList();
        List<Long> eventsIds = events.stream().map(Event::getId).toList();

        if (!eventsIds.isEmpty()) {
            events = setConfirmedRequests(events);
            Map<Long,String> savedUris = saveStats(httpServletRequest,events);
            List<ParticipationRequest> participationRequests = participationRequestRepository
                    .findAll(participationRequestSpecification.findByEventIdAndStatePublishedBuild(eventsIds,
                                                                                          State.PUBLISHED.toString()));
            for (Event event: events) {
                for (ParticipationRequest p: participationRequests) {
                    if (event.getId().equals(p.getId())) {
                        event.setConfirmedRequests(p.getId());
                    }
                }
            }
            events = setViews(events,savedUris);
        }

        if (onlyAvailable.booleanValue()) {
           events = events.stream()
                   .filter(event -> event.getParticipantLimit().longValue() > event.getConfirmedRequests()).toList();
        }
        return events.stream().map(EventMapper::toEventShortDto).toList();
    }
}