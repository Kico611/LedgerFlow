package com.ledgerflow.transaction_service.transfer;

public enum TransferStatus {
    CREATED,
    VALIDATED,
    FUNDS_RESERVED,
    SETTLED,
    FAILED,
    REVERSED
}