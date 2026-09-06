package com.ledgerflow.transaction_service.account;

import com.ledgerflow.transaction_service.account.dto.AccountResponse;
import com.ledgerflow.transaction_service.account.dto.CreateAccountRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(UUID accountId) {
        Account account = accountRepository
                .findById(accountId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found"));

        return toResponse(account);
    }

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {

        Account account = new Account(
                UUID.randomUUID(),
                request.currency(),
                request.initialBalance(),
                BigDecimal.ZERO,
                AccountStatus.ACTIVE
        );

        Account savedAccount = accountRepository.save(account);

        return toResponse(savedAccount);
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getCurrency(),
                account.getAvailableBalance(),
                account.getReservedBalance(),
                account.getStatus().name()
        );
    }
}