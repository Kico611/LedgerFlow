package com.ledgerflow.ledger_service.ledger;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "ledger_transactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_ledger_transaction_transfer",
                        columnNames = "transfer_id"
                )
        }
)
public class LedgerTransaction {

    @Id
    private UUID id;

    @Column(name = "transfer_id", nullable = false)
    private UUID transferId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LedgerTransactionStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    public LedgerTransaction() {
    }

    public LedgerTransaction(
            UUID id,
            UUID transferId,
            LedgerTransactionStatus status,
            Instant createdAt
    ) {
        this.id = id;
        this.transferId = transferId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTransferId() {
        return transferId;
    }

    public LedgerTransactionStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void markPosted() {
        this.status = LedgerTransactionStatus.POSTED;
    }

    public void markFailed() {
        this.status = LedgerTransactionStatus.FAILED;
    }
}