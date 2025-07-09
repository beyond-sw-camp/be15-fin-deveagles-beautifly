package com.deveagles.be15_deveagles_be.features.membership.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerSessionPass;
import java.util.Optional;

public interface CustomerSessionPassRepository {
  CustomerSessionPass save(CustomerSessionPass customerSessionPass);

  Optional<CustomerSessionPass> findById(Long customerSessionPassId);

  Optional<CustomerSessionPass> findByCustomerId(Long customerId);
}
