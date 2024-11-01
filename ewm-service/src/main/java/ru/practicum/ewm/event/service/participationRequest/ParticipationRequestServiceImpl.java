package ru.practicum.ewm.event.service.participationRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.event.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.ParticipationRequest;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.repository.ParticipationRequest.ParticipationRequestRepository;
import ru.practicum.ewm.event.repository.ParticipationRequest.ParticipationRequestSpecification;
import ru.practicum.ewm.event.repository.event.EventRepository;
import ru.practicum.ewm.event.service.event.EventService;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.user.service.UserService;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final ParticipationRequestSpecification participationRequestSpecification;

    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventService eventService;
    private final UserRepository userRepository;

    @Override
    public ParticipationRequestDto addParticipation(Long userId, Long eventId) {
        log.debug("addParticipation({}, {})", userId, eventId);
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        long confirmedRequests = participationRequestRepository.count(participationRequestSpecification
                .findAllByEventIdBuild(eventId));
        event.setConfirmedRequests(confirmedRequests);

        if (participationRequestRepository.exists(participationRequestSpecification
                .findAllByEventIdAndUserIdBuild(userId, eventId))) {
            throw new ConflictException("Пользователь уже оставлял запрос на участие в событии");
        }

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии ");
        }

        if (event.getParticipantLimit() != 0) {

            List<ParticipationRequest> participationRequests =
                    participationRequestRepository.findAll(participationRequestSpecification
                            .findAllByEventIdBuild(eventId));
            long participationRequestsSize = participationRequests.size();

            if (participationRequestsSize == event.getParticipantLimit()) {
                throw new ConflictException(
                        String.format("Нельзя добавить запрос на участие в событии с ID = %d, поскольку достигнут " +
                                "лимит запросов.", eventId));
            }
        }

        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(user);

        if (event.getRequestModeration()) {
            participationRequest.setStatusRequest(State.PENDING);
        } else {
            participationRequest.setStatusRequest(State.CONFIRMED);
        }
        if (event.getParticipantLimit() == 0) {
            participationRequest.setStatusRequest(State.CONFIRMED);
        }
        participationRequest.setEvent(event);
        participationRequest.setCreated(Instant.now());
        participationRequest = participationRequestRepository.save(participationRequest);
        log.info("Сохранена заявка на участие в событии с ID = {} пользователя с ID = {}.", eventId, userId);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    @Override
    public ParticipationRequestDto cancelParticipation(Long userId, Long requestId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + "не найден"));
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id = " + requestId + "не найден"));
        participationRequest.setStatusRequest(State.CANCELED);
        participationRequestRepository.save(participationRequest);

        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    @Override
    public List<ParticipationRequestDto> findRequests(Long userid) {
        User user = userRepository.findById(userid)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userid + "не найден"));
        List<ParticipationRequest> requests = participationRequestRepository
                .findAll(participationRequestSpecification.findAllByUserIdBuild(userid));

        return requests.stream().map(ParticipationRequestMapper::toParticipationRequestDto).toList();
    }

    private User getUserById(Long userId) {
        log.info("Запрос на получение пользователя из Базы данных по id = {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + "не найден"));
    }
}