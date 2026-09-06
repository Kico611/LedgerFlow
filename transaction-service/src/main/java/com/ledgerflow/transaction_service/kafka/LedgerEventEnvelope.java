package com.ledgerflow.transaction_service.kafka;

public record LedgerEventEnvelope(
        String eventType,
        String payload
) {
}