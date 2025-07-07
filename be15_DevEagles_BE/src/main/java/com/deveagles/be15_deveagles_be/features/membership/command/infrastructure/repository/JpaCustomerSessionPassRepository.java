package com.deveagles.be15_deveagles_be.features.membership.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerSessionPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerSessionPassRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCustomerSessionPassRepository
    extends JpaRepository<CustomerSessionPass, Long>, CustomerSessionPassRepository {}
