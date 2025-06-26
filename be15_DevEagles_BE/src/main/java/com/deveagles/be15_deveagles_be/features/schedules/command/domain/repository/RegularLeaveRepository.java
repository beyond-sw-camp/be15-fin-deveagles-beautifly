package com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.RegularLeave;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegularLeaveRepository extends JpaRepository<RegularLeave, Long> {}
