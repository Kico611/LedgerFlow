package com.ledgerflow.transaction_service.transfer;

import com.ledgerflow.transaction_service.dto.CreateTransferRequest;
import com.ledgerflow.transaction_service.dto.TransferResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse createTransfer(
            @Valid @RequestBody CreateTransferRequest request
    ) {
        return transferService.createTransfer(request);
    }

    @GetMapping("/{id}")
    public TransferResponse getTransferById(
            @PathVariable UUID id
    ) {
        return transferService.getTransferById(id);
    }
}