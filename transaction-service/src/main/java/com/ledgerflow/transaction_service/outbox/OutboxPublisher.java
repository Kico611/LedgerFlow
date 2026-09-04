package com.ledgerflow.transaction_service.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class OutboxPublisher {

    private static final String TOPIC = "ledgerflow.transfer-events";

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 2000)
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                outboxEventRepository.findByPublishedAtIsNull();

        for (OutboxEvent event : events) {

            try {
                kafkaTemplate.send(
                        TOPIC,
                        event.getAggregateId().toString(),
                        event.getPayload()
                ).get(10, TimeUnit.SECONDS);

                event.markPublished();
                outboxEventRepository.save(event);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;

            } catch (ExecutionException | TimeoutException e) {
                System.err.println(
                        "Failed to publish outbox event: " + event.getId()
                );
            }
        }
    }
}