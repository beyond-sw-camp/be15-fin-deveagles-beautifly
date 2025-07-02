package com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {}
