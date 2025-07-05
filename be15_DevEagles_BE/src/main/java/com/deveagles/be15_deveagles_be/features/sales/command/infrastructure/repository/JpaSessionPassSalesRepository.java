package com.deveagles.be15_deveagles_be.features.sales.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SessionPassSales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.SessionPassSalesRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSessionPassSalesRepository
    extends JpaRepository<SessionPassSales, Long>, SessionPassSalesRepository {}
