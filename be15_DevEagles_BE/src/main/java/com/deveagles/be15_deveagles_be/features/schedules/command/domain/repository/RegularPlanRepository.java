package com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.RegularPlan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegularPlanRepository extends JpaRepository<RegularPlan, Long> {}
