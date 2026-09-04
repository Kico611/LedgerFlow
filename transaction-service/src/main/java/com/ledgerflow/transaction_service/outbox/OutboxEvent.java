package com.ledgerflow.transaction_service.outbox;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private Instant createdAt;
    @Column
    private Instant publishedAt;

    public OutboxEvent() {
    }

    public OutboxEvent(
            UUID id,
            String eventType,
            UUID aggregateId,
            String payload,
            Instant createdAt
    ) {
        this.id = id;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
        this.payload = payload;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
    public Instant getPublishedAt() {
        return publishedAt;
    }
    public void markPublished() {
        this.publishedAt = Instant.now();
    }
}