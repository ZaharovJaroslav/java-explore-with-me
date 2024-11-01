package ru.practicum.ewm.event.repository.event;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.dto.EventFilter;
import ru.practicum.ewm.event.dto.EventFilterPublic;
import ru.practicum.ewm.event.mapper.DataTimeMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import java.util.List;

@Component
public class EventSpecification {
    public Specification<Event> findByEventFilterBuild(EventFilter param) {
        return hasUserIdsEqual(param.getUserIds())
                .and(hasStatEqual(param.getStates()))
                .and(hasCategoryIdEqual(param.getCategories()))
                .and(hasRangeStartEqual(param.getRangeStart(), param.getRangeEnd()));
    }

    public Specification<Event> findByEventFilterPublicBuild(EventFilterPublic param) {
        return  hasEventStateEqual(param.getState().toString())
               .and(hasRangeStartEqual(param.getRangeStart(),param.getRangeEnd()))
               .and(hasPaidEqual(param.getPaid()))
               .and(hasCategoryIdEqual(param.getCategories()))
               .and(hasEventAnnotationEqual(param.getText())
                       .or(hasEventDescriptionEqual(param.getText())));
  }

    public Specification<Event> hasEventStateEqual(String published) {
        return ((root, query, criteriaBuilder) -> published == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("state"),published));
    }

    public Specification<Event> hasEventAnnotationEqual(String text) {
        return ((root, query, criteriaBuilder) -> text == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.like(root.get("annotation"), "%" + text + "%"));
    }

    public Specification<Event> hasEventDescriptionEqual(String text) {
        return ((root, query, criteriaBuilder) -> text == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.like(root.get("description"),"%" + text + "%"));
    }

    public Specification<Event> hasPaidEqual(Boolean paid) {
        return ((root, query, criteriaBuilder) -> paid == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("paid"),paid));
    }

    public Specification<Event> findByUserIdBuild(Long userId) {
        return hasUserIdPrivatewEqual(userId);
    }

    public Specification<Event> findByEventIdsBuild(List<Long> ids) {
                return hasEventIdsEqual(ids);
    }

    public Specification<Event> hasEventIdsEqual(List<Long> ids) {
        return ((root, query, criteriaBuilder) -> ids == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("id"),ids.getFirst()));
    }

    public Specification<Event> hasUserIdPrivatewEqual(Long id) {
        return ((root, query, criteriaBuilder) -> id == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("initiator").get("id"),id));
    }

    public Specification<Event> hasUserIdsEqual(List<Long> ids) {
        return ((root, query, criteriaBuilder) -> ids == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("initiator").get("id"),ids.getFirst()));
    }

    public Specification<Event> hasStatEqual(List<State> stats) {
        return ((root, query, criteriaBuilder) ->  stats.isEmpty() ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("state"),stats.getFirst()));
    }

    public  Specification<Event> hasCategoryIdEqual(List<Long> ids) {
        return ((root, query, criteriaBuilder) ->  ids == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("category").get("id"),ids.getFirst()));
    }

    public Specification<Event> hasRangeStartEqual(String rangeStart, String rangEnd) {
        return ((root, query, criteriaBuilder) ->
                rangeStart == null || rangEnd == null ? criteriaBuilder.conjunction() :
                        criteriaBuilder.between(root.get("eventDate"), DataTimeMapper.toInstant(rangeStart),
                                                                       DataTimeMapper.toInstant(rangEnd)));
    }
}