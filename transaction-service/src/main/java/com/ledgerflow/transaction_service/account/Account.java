package com.ledgerflow.transaction_service.account;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private UUID id;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal availableBalance;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal reservedBalance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    public Account() {
    }

    public Account(
            UUID id,
            String currency,
            BigDecimal availableBalance,
            BigDecimal reservedBalance,
            AccountStatus status
    ) {
        this.id = id;
        this.currency = currency;
        this.availableBalance = availableBalance;
        this.reservedBalance = reservedBalance;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getReservedBalance() {
        return reservedBalance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void settleReserved(BigDecimal amount) {
        this.reservedBalance = this.reservedBalance.subtract(amount);
    }

    public void credit(BigDecimal amount) {
        this.availableBalance = this.availableBalance.add(amount);
    }

    public void reserve(BigDecimal amount) {
        this.availableBalance = this.availableBalance.subtract(amount);
        this.reservedBalance = this.reservedBalance.add(amount);
    }
    public void releaseReserved(BigDecimal amount) {
        reservedBalance = reservedBalance.subtract(amount);
        availableBalance = availableBalance.add(amount);
    }
    public boolean hasSufficientAvailableBalance(BigDecimal amount) {
        return availableBalance.compareTo(amount) >= 0;
    }
}