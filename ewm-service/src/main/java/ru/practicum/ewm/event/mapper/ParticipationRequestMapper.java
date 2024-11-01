package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.event.dto.ParticipationRequestDto;
import ru.practicum.ewm.event.model.ParticipationRequest;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .created(DataTimeMapper.instatToString(participationRequest.getCreated()))
                .event(participationRequest.getEvent().getId())
                .id(participationRequest.getId())
                .requester(participationRequest.getRequester().getId())
                .status(participationRequest.getStatusRequest().toString())
                .build();
    }
}
