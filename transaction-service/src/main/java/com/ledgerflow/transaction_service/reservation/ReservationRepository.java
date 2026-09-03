package com.ledgerflow.transaction_service.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    Optional<Reservation> findByTransferId(UUID transferId);
}