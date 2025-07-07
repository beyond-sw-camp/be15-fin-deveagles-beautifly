package com.deveagles.be15_deveagles_be.features.membership.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerSessionPass;

public interface CustomerSessionPassRepository {
  CustomerSessionPass save(CustomerSessionPass customerSessionPass);
}
