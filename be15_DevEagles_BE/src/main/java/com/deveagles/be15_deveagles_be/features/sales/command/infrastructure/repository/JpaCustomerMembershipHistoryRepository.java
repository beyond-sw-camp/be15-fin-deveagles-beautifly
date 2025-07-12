package com.deveagles.be15_deveagles_be.features.sales.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.CustomerMembershipHistory;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.CustomerMembershipHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCustomerMembershipHistoryRepository
    extends JpaRepository<CustomerMembershipHistory, Long>, CustomerMembershipHistoryRepository {}
