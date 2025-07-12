package com.deveagles.be15_deveagles_be.features.sales.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.CustomerMembershipHistory;
import java.util.Optional;

public interface CustomerMembershipHistoryRepository {
  CustomerMembershipHistory save(CustomerMembershipHistory build);

  Optional<CustomerMembershipHistory> findBySalesIdAndPaymentsId(Long salesId, Long paymentsId);
}
