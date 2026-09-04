package com.ledgerflow.ledger_service.kafka;

import java.math.BigDecimal;
import java.util.UUID;

public record FundsReservedEvent(
        UUID transferId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String currency
) {
}