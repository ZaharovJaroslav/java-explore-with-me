package ru.practicum.ewm.request.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;

@Component
public class RequestMapper {

    public ParticipationRequestDto toDto(Request request) {
        ParticipationRequestDto participationRequestDto = new ParticipationRequestDto();
        participationRequestDto.setId(request.getId());
        participationRequestDto.setRequester(request.getRequester().getId());
        participationRequestDto.setEvent(request.getEvent().getId());
        participationRequestDto.setCreated(request.getCreated());
        participationRequestDto.setStatus(String.valueOf(request.getStatus()));
        return participationRequestDto;
    }
}
