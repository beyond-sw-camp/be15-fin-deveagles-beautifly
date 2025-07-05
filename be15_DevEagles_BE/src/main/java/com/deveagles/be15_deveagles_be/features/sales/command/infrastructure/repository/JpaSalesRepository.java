package com.deveagles.be15_deveagles_be.features.sales.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.Sales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.SalesRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSalesRepository extends JpaRepository<Sales, Long>, SalesRepository {}
