package com.deveagles.be15_deveagles_be.features.sales.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.Payments;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.PaymentsRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPaymentsRepository extends JpaRepository<Payments, Long>, PaymentsRepository {}
