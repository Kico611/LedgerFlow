package com.ledgerflow.transaction_service.reservation;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationRepository reservationRepository;

    public ReservationController(
            ReservationRepository reservationRepository
    ) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
}