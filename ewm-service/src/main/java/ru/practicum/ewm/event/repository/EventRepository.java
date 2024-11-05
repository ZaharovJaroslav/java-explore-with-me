package ru.practicum.ewm.event.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query("SELECT e FROM Event e " +
            "WHERE e.initiator.id IN (:users) " +
            "AND e.state IN (:states) " +
            "AND e.category.id IN (:categories) " +
            "AND e.eventDate BETWEEN :rangeStart AND :rangeEnd")
    List<Event> findEventsByParams(
            @Param("users") List<Long> users,
            @Param("states") List<String> states,
            @Param("categories") List<Long> categories,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Event findByInitiatorIdAndId(Long userId, Long eventId);

    List<Event> findByIdIn(List<Long> ids);

    Event findAllByCategoryId(Long catId);

    List<Event> findAllByIdIn(List<Long> ids);

    Optional<Event> findByIdAndState(Long id, EventState state);

    List<Event> findByInitiatorIdAndState(long userId, EventState state, Pageable pageable);

    List<Event> findByStateAndInitiatorIdIn(EventState state, List<Long> usersIds, Pageable pageable);
}
