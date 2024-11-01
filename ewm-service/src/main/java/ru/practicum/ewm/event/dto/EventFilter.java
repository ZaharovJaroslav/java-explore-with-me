package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.model.State;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EventFilter {
    private List<Long> userIds;
    private List<State> states;
    private List<Long> categories;
    private String rangeStart;
    private String rangeEnd;
}