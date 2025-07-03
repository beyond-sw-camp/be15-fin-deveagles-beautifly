package com.deveagles.be15_deveagles_be.features.items.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;

public interface PrimaryItemRepository {
  PrimaryItem save(PrimaryItem primaryItem);

  Optional<PrimaryItem> findById(Long id);

  @Query("SELECT p FROM PrimaryItem p WHERE p.shopId.shopId = :shopId AND p.deletedAt IS NULL")
  List<PrimaryItem> findAllByShopId(@Param("shopId") Long shopId);
}
