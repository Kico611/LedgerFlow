package com.ledgerflow.ledger_service.kafka;

public record LedgerEventEnvelope(
        String eventType,
        String payload
) {
}