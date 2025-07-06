package com.deveagles.be15_deveagles_be.features.sales.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PrepaidPassSales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.PrepaidPassSalesRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPrepaidPassSalesRepository
    extends JpaRepository<PrepaidPassSales, Long>, PrepaidPassSalesRepository {}
