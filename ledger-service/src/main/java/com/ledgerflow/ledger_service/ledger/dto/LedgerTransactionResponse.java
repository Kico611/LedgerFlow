package com.ledgerflow.ledger_service.ledger.dto;

import java.time.Instant;
import java.util.UUID;

public record LedgerTransactionResponse(
        UUID id,
        UUID transferId,
        String status,
        Instant createdAt
) {
}