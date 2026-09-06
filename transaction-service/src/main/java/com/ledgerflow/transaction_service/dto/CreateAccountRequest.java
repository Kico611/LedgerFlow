package com.ledgerflow.transaction_service.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotBlank
        String currency,

        @NotNull
        @PositiveOrZero
        BigDecimal initialBalance

) {
}