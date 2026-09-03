package com.ledgerflow.transaction_service.transfer;

import com.ledgerflow.transaction_service.transfer.dto.CreateTransferRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transfer createTransfer(
            @Valid @RequestBody CreateTransferRequest request
    ) {
        return transferService.createTransfer(request);
    }
}