package ru.practicum.ewm.request.service;


import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto addRequestPrivate(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestsPrivate(Long userId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);
}