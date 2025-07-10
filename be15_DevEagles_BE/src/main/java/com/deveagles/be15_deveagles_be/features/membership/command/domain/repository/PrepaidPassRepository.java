package com.deveagles.be15_deveagles_be.features.membership.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.PrepaidPass;
import java.util.List;
import java.util.Optional;

public interface PrepaidPassRepository {
  PrepaidPass save(PrepaidPass prepaidPass);

  Optional<PrepaidPass> findById(Long prepaidPassId);

  List<PrepaidPass> findAllByShopId_ShopIdAndDeletedAtIsNull(Long shopId);
}
