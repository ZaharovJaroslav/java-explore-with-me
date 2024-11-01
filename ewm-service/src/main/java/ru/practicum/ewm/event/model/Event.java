package ru.practicum.ewm.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;
import java.time.Instant;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Data
@Table(name = "events")

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_on", nullable = false)
    Instant createdOn;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private Instant eventDate;

    @Embedded
    private Location location;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    @Column(name = "published_on")
    private Instant publishedOn;

    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(name = "title", nullable = false)
    private String title;

    @Transient
    private Long confirmedRequests;

    @Transient
    private Long views;

    @PrePersist
    public void prePersist() {
        if (paid == null) {
            paid = Boolean.FALSE;
        }
        if (requestModeration == null) {
            requestModeration = Boolean.TRUE;
        }
        if (participantLimit == null) {
            participantLimit = 0;
        }
    }
}