package com.deveagles.be15_deveagles_be.features.items.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import java.util.List;
import java.util.Optional;

public interface PrimaryItemRepository {
  PrimaryItem save(PrimaryItem primaryItem);

  Optional<PrimaryItem> findById(Long id);

  List<PrimaryItem> findAll();
}
