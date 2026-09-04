package com.ledgerflow.transaction_service.reservation;

import com.ledgerflow.transaction_service.account.Account;
import com.ledgerflow.transaction_service.transfer.Transfer;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "reservations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reservation_transfer",
                        columnNames = "transfer_id"
                )
        }
)
public class Reservation {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transfer_id", nullable = false)
    private Transfer transfer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    public Reservation() {
    }

    public Reservation(
            UUID id,
            Transfer transfer,
            Account account,
            BigDecimal amount,
            ReservationStatus status
    ) {
        this.id = id;
        this.transfer = transfer;
        this.account = account;
        this.amount = amount;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public Account getAccount() {
        return account;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void markSettled() {
        this.status = ReservationStatus.SETTLED;
    }
}