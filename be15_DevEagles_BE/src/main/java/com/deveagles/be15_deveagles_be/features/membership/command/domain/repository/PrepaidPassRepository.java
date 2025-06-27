package com.deveagles.be15_deveagles_be.features.membership.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.PrepaidPass;

public interface PrepaidPassRepository {
  PrepaidPass save(PrepaidPass prepaidPass);
}
