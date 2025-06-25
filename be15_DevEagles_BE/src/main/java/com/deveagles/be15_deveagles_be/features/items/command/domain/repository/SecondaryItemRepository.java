package com.deveagles.be15_deveagles_be.features.items.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.SecondaryItem;

public interface SecondaryItemRepository {
  SecondaryItem save(SecondaryItem secondaryItem);
}
