package com.deveagles.be15_deveagles_be.features.membership.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.SessionPass;
import java.util.List;
import java.util.Optional;

public interface SessionPassRepository {
  SessionPass save(SessionPass sessionPass);

  Optional<SessionPass> findById(Long sessionPassId);

  List<SessionPass> findAllByShopId_ShopIdAndDeletedAtIsNull(Long shopId);
}
