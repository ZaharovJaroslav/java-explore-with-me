package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.user.dto.UserShortDto;

@Builder
@Getter
@Setter
public class EventFullDto {
    private String annotation;
    private Category category;
    private Long confirmedRequests;
    private String createdOn;
    private String description;
    private String eventDate;
    private Long id;
    private UserShortDto initiator;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private String publishedOn;
    private Boolean requestModeration;
    private State state;
    private String title;
    private Long views;
}
