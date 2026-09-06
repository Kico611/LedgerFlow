package com.ledgerflow.ledger_service.outbox;

import com.ledgerflow.ledger_service.kafka.LedgerEventEnvelope;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class OutboxPublisher {

    private static final String TOPIC = "ledgerflow.ledger-events";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 2000)
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxEventRepository.findByPublishedAtIsNull();

        for (OutboxEvent event : events) {

            try {
                LedgerEventEnvelope envelope =
                        new LedgerEventEnvelope(
                                event.getEventType(),
                                event.getPayload()
                        );

                String message =
                        objectMapper.writeValueAsString(envelope);

                kafkaTemplate.send(
                        TOPIC,
                        event.getAggregateId().toString(),
                        message
                ).get(10, TimeUnit.SECONDS);

                event.markPublished();
                outboxEventRepository.save(event);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;

            } catch (ExecutionException | TimeoutException e) {
                System.err.println(
                        "Failed to publish ledger outbox event: "
                                + event.getId()
                );

            } catch (Exception e) {
                System.err.println(
                        "Failed to serialize ledger outbox event: "
                                + event.getId()
                );
            }
        }
    }
}