package com.ledgerflow.transaction_service.transfer;

import com.ledgerflow.transaction_service.account.Account;
import com.ledgerflow.transaction_service.account.AccountRepository;
import com.ledgerflow.transaction_service.transfer.dto.CreateTransferRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ledgerflow.transaction_service.account.AccountStatus;
import com.ledgerflow.transaction_service.reservation.Reservation;
import com.ledgerflow.transaction_service.reservation.ReservationRepository;
import com.ledgerflow.transaction_service.reservation.ReservationStatus;
import com.ledgerflow.transaction_service.outbox.OutboxEvent;
import com.ledgerflow.transaction_service.outbox.OutboxEventRepository;
import java.time.Instant;
import java.util.UUID;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final ReservationRepository reservationRepository;
    private final OutboxEventRepository outboxEventRepository;

    public TransferService(
            TransferRepository transferRepository,
            AccountRepository accountRepository,
            ReservationRepository reservationRepository,
            OutboxEventRepository outboxEventRepository
    ) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.reservationRepository = reservationRepository;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public Transfer createTransfer(CreateTransferRequest request) {

        Account sourceAccount = accountRepository
                .findByIdForUpdate(request.sourceAccountId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Source account not found"));

        Account destinationAccount = accountRepository
                .findById(request.destinationAccountId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Destination account not found"));

        if (sourceAccount.getId().equals(destinationAccount.getId())) {
            throw new IllegalArgumentException(
                    "Source and destination account must be different"
            );
        }

        if (sourceAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Source account is not active"
            );
        }

        if (destinationAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Destination account is not active"
            );
        }

        if (!sourceAccount.getCurrency().equals(destinationAccount.getCurrency())) {
            throw new IllegalArgumentException(
                    "Accounts must use the same currency"
            );
        }

        if (!sourceAccount.hasSufficientAvailableBalance(request.amount())) {
            throw new IllegalArgumentException(
                    "Insufficient available balance"
            );
        }

        Transfer transfer = new Transfer(
                UUID.randomUUID(),
                sourceAccount,
                destinationAccount,
                request.amount(),
                sourceAccount.getCurrency(),
                TransferStatus.CREATED
        );

        transfer.setStatus(TransferStatus.VALIDATED);

        sourceAccount.reserve(request.amount());

        transfer.setStatus(TransferStatus.FUNDS_RESERVED);

        transferRepository.save(transfer);

        Reservation reservation = new Reservation(
                UUID.randomUUID(),
                transfer,
                sourceAccount,
                request.amount(),
                ReservationStatus.ACTIVE
        );

        reservationRepository.save(reservation);

        String payload = """
                {
                  "transferId": "%s",
                  "sourceAccountId": "%s",
                  "destinationAccountId": "%s",
                  "amount": %s,
                  "currency": "%s"
                }
                """.formatted(
                transfer.getId(),
                sourceAccount.getId(),
                destinationAccount.getId(),
                transfer.getAmount(),
                transfer.getCurrency()
        );

        OutboxEvent outboxEvent = new OutboxEvent(
                UUID.randomUUID(),
                "FUNDS_RESERVED",
                transfer.getId(),
                payload,
                Instant.now()
        );

        outboxEventRepository.save(outboxEvent);

        return transfer;
    }

    @Transactional
    public void settleTransfer(UUID transferId) {

        Transfer transfer = transferRepository
                .findById(transferId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Transfer not found"));

        if (transfer.getStatus() == TransferStatus.SETTLED) {
            return;
        }

        Reservation reservation = reservationRepository
                .findByTransferId(transferId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Reservation not found"));

        Account sourceAccount = accountRepository
                .findByIdForUpdate(transfer.getSourceAccount().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Source account not found"));

        Account destinationAccount = accountRepository
                .findByIdForUpdate(transfer.getDestinationAccount().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Destination account not found"));

        sourceAccount.settleReserved(reservation.getAmount());

        destinationAccount.credit(reservation.getAmount());

        reservation.markSettled();

        transfer.setStatus(TransferStatus.SETTLED);

        reservationRepository.save(reservation);
        transferRepository.save(transfer);
    }
}