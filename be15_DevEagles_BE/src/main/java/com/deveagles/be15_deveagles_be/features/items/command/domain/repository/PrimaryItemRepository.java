package com.deveagles.be15_deveagles_be.features.items.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;

public interface PrimaryItemRepository {
  PrimaryItem save(PrimaryItem primaryItem);
}
