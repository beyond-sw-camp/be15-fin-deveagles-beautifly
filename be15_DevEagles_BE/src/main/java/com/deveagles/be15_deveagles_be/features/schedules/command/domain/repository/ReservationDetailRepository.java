package com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationDetailRepository extends JpaRepository<ReservationDetail, Long> {
  void deleteByReservationId(Long reservationId);
}
