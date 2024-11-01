package ru.practicum.ewm.event.repository.ParticipationRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.ParticipationRequest;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long>,
                                                        JpaSpecificationExecutor<ParticipationRequest> {
}
