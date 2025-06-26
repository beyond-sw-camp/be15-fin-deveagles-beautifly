package com.deveagles.be15_deveagles_be.features.items.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.SecondaryItem;
import java.util.List;

public interface SecondaryItemRepository {
  SecondaryItem save(SecondaryItem secondaryItem);

  List<SecondaryItem> findAllByDeletedAtIsNull();
}
