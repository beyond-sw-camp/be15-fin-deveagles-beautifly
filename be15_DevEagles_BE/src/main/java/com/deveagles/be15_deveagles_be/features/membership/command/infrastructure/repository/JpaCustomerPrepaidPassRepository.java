package com.deveagles.be15_deveagles_be.features.membership.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerPrepaidPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerPrepaidPassRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCustomerPrepaidPassRepository
    extends JpaRepository<CustomerPrepaidPass, Long>, CustomerPrepaidPassRepository {}
