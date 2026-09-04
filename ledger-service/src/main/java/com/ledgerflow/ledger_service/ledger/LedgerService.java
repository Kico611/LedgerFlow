package com.ledgerflow.ledger_service.ledger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import com.ledgerflow.ledger_service.outbox.OutboxEvent;
import com.ledgerflow.ledger_service.outbox.OutboxEventRepository;

@Service
public class LedgerService {

    private final LedgerTransactionRepository ledgerTransactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final OutboxEventRepository outboxEventRepository;

    public LedgerService(
            LedgerTransactionRepository ledgerTransactionRepository,
            LedgerEntryRepository ledgerEntryRepository,
            OutboxEventRepository outboxEventRepository
    ) {
        this.ledgerTransactionRepository = ledgerTransactionRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional
    public void createLedgerEntries(
            UUID transferId,
            UUID sourceAccountId,
            UUID destinationAccountId,
            BigDecimal amount,
            String currency
    ) {

        if (ledgerTransactionRepository.findByTransferId(transferId).isPresent()) {
            return;
        }

        LedgerTransaction ledgerTransaction = new LedgerTransaction(
                UUID.randomUUID(),
                transferId,
                LedgerTransactionStatus.CREATED,
                Instant.now()
        );

        ledgerTransaction =
                ledgerTransactionRepository.save(ledgerTransaction);

        LedgerEntry debitEntry = new LedgerEntry(
                UUID.randomUUID(),
                ledgerTransaction,
                sourceAccountId,
                LedgerEntryType.DEBIT,
                amount,
                currency
        );

        LedgerEntry creditEntry = new LedgerEntry(
                UUID.randomUUID(),
                ledgerTransaction,
                destinationAccountId,
                LedgerEntryType.CREDIT,
                amount,
                currency
        );

        ledgerEntryRepository.save(debitEntry);
        ledgerEntryRepository.save(creditEntry);

        ledgerTransaction.markPosted();
        ledgerTransactionRepository.save(ledgerTransaction);

        String payload = """
        {
          "transferId": "%s",
          "ledgerTransactionId": "%s"
        }
        """.formatted(
                transferId,
                ledgerTransaction.getId()
        );

        OutboxEvent outboxEvent = new OutboxEvent(
                UUID.randomUUID(),
                "LEDGER_POSTED",
                transferId,
                payload,
                Instant.now()
        );

        outboxEventRepository.save(outboxEvent);
    }
}