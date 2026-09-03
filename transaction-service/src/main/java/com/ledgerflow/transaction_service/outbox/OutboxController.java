package com.ledgerflow.transaction_service.outbox;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/outbox")
public class OutboxController {

    private final OutboxEventRepository outboxEventRepository;

    public OutboxController(OutboxEventRepository outboxEventRepository) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @GetMapping
    public List<OutboxEvent> getAllEvents() {
        return outboxEventRepository.findAll();
    }
}