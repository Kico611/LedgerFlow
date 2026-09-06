package com.ledgerflow.ledger_service.ledger;

import com.ledgerflow.ledger_service.outbox.OutboxEvent;
import com.ledgerflow.ledger_service.outbox.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class LedgerFailureService {

    private final OutboxEventRepository outboxEventRepository;

    public LedgerFailureService(
            OutboxEventRepository outboxEventRepository
    ) {
        this.outboxEventRepository = outboxEventRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleFailure(UUID transferId, String reason) {

        String payload = """
        {
          "transferId": "%s",
          "reason": "%s"
        }
        """.formatted(
                transferId,
                reason
        );

        OutboxEvent outboxEvent = new OutboxEvent(
                UUID.randomUUID(),
                "LEDGER_FAILED",
                transferId,
                payload,
                Instant.now()
        );

        outboxEventRepository.save(outboxEvent);
    }
}