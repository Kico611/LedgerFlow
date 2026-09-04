package com.ledgerflow.ledger_service.outbox;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/outbox")
public class OutboxController {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxController(
            OutboxEventRepository outboxEventRepository
    ) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @GetMapping
    public List<OutboxEvent> getAllEvents() {
        return outboxEventRepository.findAll();
    }
}