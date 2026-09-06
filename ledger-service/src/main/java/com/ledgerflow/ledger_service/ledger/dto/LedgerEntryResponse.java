package com.ledgerflow.ledger_service.ledger.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LedgerEntryResponse(
        UUID id,
        UUID ledgerTransactionId,
        UUID accountId,
        String type,
        BigDecimal amount,
        String currency
) {
}