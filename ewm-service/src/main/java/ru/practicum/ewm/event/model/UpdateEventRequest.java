package ru.practicum.ewm.event.model;

import lombok.Builder;
import lombok.Getter;

    @Builder
    @Getter
    public class UpdateEventRequest {
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