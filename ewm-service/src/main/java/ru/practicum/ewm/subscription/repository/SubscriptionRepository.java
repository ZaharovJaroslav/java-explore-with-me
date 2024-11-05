package ru.practicum.ewm.subscription.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.subscription.model.Subscription;
import ru.practicum.ewm.user.model.User;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsBySubscriberAndSubscribedTo(User user, User author);

    Subscription findBySubscriberIdAndSubscribedToId(Long userId, Long authorId);

    List<Subscription> findBySubscriberId(Long userId, Pageable pageable);
}
