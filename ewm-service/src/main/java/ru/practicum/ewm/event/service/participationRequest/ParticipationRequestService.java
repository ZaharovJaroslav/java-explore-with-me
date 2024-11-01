package ru.practicum.ewm.event.service.participationRequest;

import ru.practicum.ewm.event.dto.ParticipationRequestDto;
import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto addParticipation(Long userid, Long eventId);

    ParticipationRequestDto cancelParticipation(Long userId, Long requestId);

    List<ParticipationRequestDto> findRequests(Long userid);
}
