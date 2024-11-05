package ru.practicum.ewm.subscription.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import ru.practicum.ewm.user.model.User;

@Data
@Entity
@Table(name = "subscriptions")
@IdClass(SubscriptionId.class)
public class Subscription {
    @Id
    @ManyToOne
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    @Id
    @ManyToOne
    @JoinColumn(name = "subscribed_to_id")
    private User subscribedTo;
}