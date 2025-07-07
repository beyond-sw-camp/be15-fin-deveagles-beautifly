package com.deveagles.be15_deveagles_be.features.membership.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerPrepaidPass;

public interface CustomerPrepaidPassRepository {
  CustomerPrepaidPass save(CustomerPrepaidPass customerPrepaidPass);
}
