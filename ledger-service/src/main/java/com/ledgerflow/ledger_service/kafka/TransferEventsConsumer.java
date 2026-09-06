package com.ledgerflow.ledger_service.kafka;

import com.ledgerflow.ledger_service.ledger.LedgerFailureService;
import com.ledgerflow.ledger_service.ledger.LedgerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class TransferEventsConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(TransferEventsConsumer.class);

    private final ObjectMapper objectMapper;
    private final LedgerService ledgerService;
    private final LedgerFailureService ledgerFailureService;

    public TransferEventsConsumer(
            ObjectMapper objectMapper,
            LedgerService ledgerService,
            LedgerFailureService ledgerFailureService
    ) {
        this.objectMapper = objectMapper;
        this.ledgerService = ledgerService;
        this.ledgerFailureService = ledgerFailureService;
    }

    @KafkaListener(
            topics = "ledgerflow.transfer-events",
            groupId = "ledger-service"
    )
    public void consume(String payload) {

        FundsReservedEvent event;

        try {
            event = objectMapper.readValue(
                    payload,
                    FundsReservedEvent.class
            );
        } catch (Exception e) {

            log.error(
                    "Failed to deserialize transfer event: {}",
                    payload,
                    e
            );

            // Važno:
            // exception ide Spring Kafka error handleru
            // -> retry
            // -> DLT
            throw new RuntimeException(
                    "Invalid transfer event payload",
                    e
            );
        }

        try {

            ledgerService.createLedgerEntries(
                    event.transferId(),
                    event.sourceAccountId(),
                    event.destinationAccountId(),
                    event.amount(),
                    event.currency()
            );

        } catch (Exception e) {

            log.error(
                    "Ledger processing failed for transfer {}",
                    event.transferId(),
                    e
            );

            ledgerFailureService.handleFailure(
                    event.transferId(),
                    e.getMessage()
            );
        }
    }
}