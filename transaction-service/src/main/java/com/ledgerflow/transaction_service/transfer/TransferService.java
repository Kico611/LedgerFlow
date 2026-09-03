package com.ledgerflow.transaction_service.transfer;

import com.ledgerflow.transaction_service.account.Account;
import com.ledgerflow.transaction_service.account.AccountRepository;
import com.ledgerflow.transaction_service.transfer.dto.CreateTransferRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;

    public TransferService(
            TransferRepository transferRepository,
            AccountRepository accountRepository
    ) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    public Transfer createTransfer(CreateTransferRequest request) {

        Account sourceAccount = accountRepository
                .findById(request.sourceAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account destinationAccount = accountRepository
                .findById(request.destinationAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (sourceAccount.getId().equals(destinationAccount.getId())) {
            throw new IllegalArgumentException("Source and destination account must be different");
        }

        Transfer transfer = new Transfer(
                UUID.randomUUID(),
                sourceAccount,
                destinationAccount,
                request.amount(),
                sourceAccount.getCurrency(),
                TransferStatus.CREATED
        );

        return transferRepository.save(transfer);
    }
}