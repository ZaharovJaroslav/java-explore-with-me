package ru.practicum.ewm.event.mapper;

import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import java.time.Instant;

public class EventMapper {
    public static Event toEvent(User user, Category category, NewEventDto eventDto) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .category(category)
                .createdOn(Instant.now())
                .description(eventDto.getDescription())
                .eventDate(DataTimeMapper.toInstant(eventDto.getEventDate()))
                .location(eventDto.getLocation())
                .initiator(user)
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .state(State.PENDING)
                .title(eventDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDtoWhenCreated(Event event) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .createdOn(DataTimeMapper.instatToString(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(DataTimeMapper.instatToString(event.getEventDate()))
                .id(event.getId())
                .initiator(UserMapper.touserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(DataTimeMapper.instatToString(event.getCreatedOn()))
                .description(event.getDescription())
                .eventDate(DataTimeMapper.instatToString(event.getEventDate()))
                .id(event.getId())
                .initiator(UserMapper.touserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(DataTimeMapper.instatToString(event.getPublishedOn()))
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(DataTimeMapper.instatToString(event.getEventDate()))
                .id(event.getId())
                .initiator(UserMapper.touserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}