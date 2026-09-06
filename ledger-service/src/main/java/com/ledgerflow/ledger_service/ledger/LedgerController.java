package com.ledgerflow.ledger_service.ledger;

import com.ledgerflow.ledger_service.ledger.dto.LedgerEntryResponse;
import com.ledgerflow.ledger_service.ledger.dto.LedgerTransactionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping("/transactions")
    public List<LedgerTransactionResponse> getTransactions() {
        return ledgerService.getTransactions();
    }

    @GetMapping("/entries")
    public List<LedgerEntryResponse> getEntries() {
        return ledgerService.getEntries();
    }
}