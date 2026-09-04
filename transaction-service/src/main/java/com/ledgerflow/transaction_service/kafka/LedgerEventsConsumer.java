package com.ledgerflow.transaction_service.kafka;

import com.ledgerflow.transaction_service.transfer.TransferService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class LedgerEventsConsumer {

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
    public void consume(String payload) throws JacksonException {

        LedgerPostedEvent event =
                objectMapper.readValue(payload, LedgerPostedEvent.class);

        transferService.settleTransfer(event.transferId());
    }
}