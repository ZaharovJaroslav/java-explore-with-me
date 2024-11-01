package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.model.UpdateEventRequest;

public class UpdateEventRequestMapper {
    public static UpdateEventRequest fromAdmintoUpdateEventRequest(UpdateEventAdminRequest updateEventAdminRequest) {
        return UpdateEventRequest.builder()
                .annotation(updateEventAdminRequest.getAnnotation())
                .category(updateEventAdminRequest.getCategory())
                .description(updateEventAdminRequest.getDescription())
                .eventDate(updateEventAdminRequest.getEventDate())
                .location(updateEventAdminRequest.getLocation())
                .paid(updateEventAdminRequest.getPaid())
                .participantLimit(updateEventAdminRequest.getParticipantLimit())
                .requestModeration(updateEventAdminRequest.getRequestModeration())
                .stateAction(updateEventAdminRequest.getStateAction())
                .title(updateEventAdminRequest.getTitle())
                .build();
    }

    public static UpdateEventRequest fromUsertoUpdateEventRequest(UpdateEventUserRequest updateEventUserRequest) {
        return UpdateEventRequest.builder()
                .annotation(updateEventUserRequest.getAnnotation())
                .category(updateEventUserRequest.getCategory())
                .description(updateEventUserRequest.getDescription())
                .eventDate(updateEventUserRequest.getEventDate())
                .location(updateEventUserRequest.getLocation())
                .paid(updateEventUserRequest.getPaid())
                .participantLimit(updateEventUserRequest.getParticipantLimit())
                .requestModeration(updateEventUserRequest.getRequestModeration())
                .stateAction(updateEventUserRequest.getStateAction())
                .title(updateEventUserRequest.getTitle())
                .build();
    }
}