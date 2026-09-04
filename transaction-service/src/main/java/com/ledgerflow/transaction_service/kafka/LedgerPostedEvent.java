package com.ledgerflow.transaction_service.kafka;

import java.util.UUID;

public record LedgerPostedEvent(
        UUID transferId,
        UUID ledgerTransactionId
) {
}