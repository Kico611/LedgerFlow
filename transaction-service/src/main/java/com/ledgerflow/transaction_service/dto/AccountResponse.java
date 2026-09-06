package com.ledgerflow.transaction_service.account.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String currency,
        BigDecimal availableBalance,
        BigDecimal reservedBalance,
        String status
) {
}