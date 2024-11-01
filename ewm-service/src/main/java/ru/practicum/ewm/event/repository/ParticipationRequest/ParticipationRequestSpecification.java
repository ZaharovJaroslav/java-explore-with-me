package ru.practicum.ewm.event.repository.ParticipationRequest;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.model.ParticipationRequest;
import java.util.List;

@Component
public class ParticipationRequestSpecification {
    public Specification<ParticipationRequest> findAllByEventIdAndUserIdBuild(Long userId, Long eventId) {
        return hasUserIdEqual(userId)
                .and(hasEventIdwEqual(eventId));
    }

    public Specification<ParticipationRequest> findAllByEventIdBuild(Long eventId) {
        return hasEventIdwEqual(eventId);
    }

    public Specification<ParticipationRequest> findAllByUserIdBuild(Long userId) {
        return hasUserIdEqual(userId);
    }

    public Specification<ParticipationRequest> findAllCanceledAndPendingBuild(Long eventId, String canceled,
                                                                              String pending) {
        return hasEventIdwEqual(eventId)
                .and(hasRequestStateCanceledUserEqual(canceled))
                .and(hasRequestStatePendingUserEqual(pending));
    }

    public Specification<ParticipationRequest> findByEventIdAndStatePublishedBuild(List<Long> ids, String published) {
        return hasRequestStatePublishedUserEqual(published)
                .and(hasEventIdEqual(ids));
    }

    public Specification<ParticipationRequest> hasRequestStatePendingUserEqual(String pending) {
        return  ((root, query, criteriaBuilder) -> pending == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("statusRequest"),pending));
    }

    public Specification<ParticipationRequest> hasRequestStateCanceledUserEqual(String canceled) {
        return  ((root, query, criteriaBuilder) -> canceled == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("statusRequest"),canceled));
    }

    public Specification<ParticipationRequest> hasUserIdEqual(Long id) {
        return  ((root, query, criteriaBuilder) -> id == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("requester").get("id"),id));
    }

    public Specification<ParticipationRequest> hasEventIdwEqual(Long id) {
        return  ((root, query, criteriaBuilder) -> id == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("event").get("id"),id));
    }

    public  Specification<ParticipationRequest> hasEventIdEqual(List<Long> ids) {
        return ((root, query, criteriaBuilder) ->  ids == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("event").get("id"),ids.getFirst()));
    }

    public Specification<ParticipationRequest> hasRequestStatePublishedUserEqual(String published) {
        return  ((root, query, criteriaBuilder) -> published == null ?
                criteriaBuilder.conjunction() : criteriaBuilder.equal(root.get("statusRequest"),published));
    }
}