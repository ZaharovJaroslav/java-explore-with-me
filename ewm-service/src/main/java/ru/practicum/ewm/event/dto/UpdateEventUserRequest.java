package ru.practicum.ewm.event.dto;

import lombok.Getter;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.StateAction;

@Getter
public class UpdateEventUserRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateAction stateAction;
    private String title;
}