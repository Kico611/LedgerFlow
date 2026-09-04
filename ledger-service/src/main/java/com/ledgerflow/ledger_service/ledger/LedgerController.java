package com.ledgerflow.ledger_service.ledger;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ledger")
public class LedgerController {

    private final LedgerTransactionRepository ledgerTransactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;

    public LedgerController(
            LedgerTransactionRepository ledgerTransactionRepository,
            LedgerEntryRepository ledgerEntryRepository
    ) {
        this.ledgerTransactionRepository = ledgerTransactionRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @GetMapping("/transactions")
    public List<LedgerTransaction> getTransactions() {
        return ledgerTransactionRepository.findAll();
    }

    @GetMapping("/entries")
    public List<LedgerEntry> getEntries() {
        return ledgerEntryRepository.findAll();
    }
}