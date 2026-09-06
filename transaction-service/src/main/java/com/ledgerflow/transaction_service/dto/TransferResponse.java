package com.ledgerflow.transaction_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(
        UUID id,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String currency,
        String status
) {
}