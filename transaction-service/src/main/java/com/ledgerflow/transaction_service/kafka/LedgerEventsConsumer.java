package com.ledgerflow.transaction_service.kafka;

import com.ledgerflow.transaction_service.transfer.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class LedgerEventsConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(LedgerEventsConsumer.class);

    private final ObjectMapper objectMapper;
    private final TransferService transferService;

    public LedgerEventsConsumer(
            ObjectMapper objectMapper,
            TransferService transferService
    ) {
        this.objectMapper = objectMapper;
        this.transferService = transferService;
    }

    @KafkaListener(
            topics = "ledgerflow.ledger-events",
            groupId = "transaction-service"
    )
    public void consume(String message) {

        try {
            LedgerEventEnvelope envelope =
                    objectMapper.readValue(
                            message,
                            LedgerEventEnvelope.class
                    );

            if (envelope.eventType() == null) {
                throw new IllegalArgumentException(
                        "Ledger event is missing eventType"
                );
            }

            switch (envelope.eventType()) {

                case "LEDGER_POSTED" -> {

                    LedgerPostedEvent event =
                            objectMapper.readValue(
                                    envelope.payload(),
                                    LedgerPostedEvent.class
                            );

                    transferService.settleTransfer(
                            event.transferId()
                    );
                }

                case "LEDGER_FAILED" -> {

                    LedgerFailedEvent event =
                            objectMapper.readValue(
                                    envelope.payload(),
                                    LedgerFailedEvent.class
                            );

                    transferService.reverseTransfer(
                            event.transferId()
                    );
                }

                default -> throw new IllegalArgumentException(
                        "Unknown ledger event type: "
                                + envelope.eventType()
                );
            }

        } catch (Exception e) {

            log.error(
                    "Failed to process ledger event: {}",
                    message,
                    e
            );

            throw new RuntimeException(
                    "Failed to process ledger event",
                    e
            );
        }
    }
}