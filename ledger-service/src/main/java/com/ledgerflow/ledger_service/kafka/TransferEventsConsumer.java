package com.ledgerflow.ledger_service.kafka;

import com.ledgerflow.ledger_service.ledger.LedgerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class TransferEventsConsumer {

    private final ObjectMapper objectMapper;
    private final LedgerService ledgerService;

    public TransferEventsConsumer(
            ObjectMapper objectMapper,
            LedgerService ledgerService
    ) {
        this.objectMapper = objectMapper;
        this.ledgerService = ledgerService;
    }

    @KafkaListener(
            topics = "ledgerflow.transfer-events",
            groupId = "ledger-service"
    )
    public void consume(String payload) throws JacksonException {

        FundsReservedEvent event =
                objectMapper.readValue(payload, FundsReservedEvent.class);

        ledgerService.createLedgerEntries(
                event.transferId(),
                event.sourceAccountId(),
                event.destinationAccountId(),
                event.amount(),
                event.currency()
        );
    }
}