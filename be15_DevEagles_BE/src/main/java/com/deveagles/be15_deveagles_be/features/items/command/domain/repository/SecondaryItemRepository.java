package com.deveagles.be15_deveagles_be.features.items.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.SecondaryItem;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;

public interface SecondaryItemRepository {
  SecondaryItem save(SecondaryItem secondaryItem);

  List<SecondaryItem> findAllByDeletedAtIsNull();

  Optional<SecondaryItem> findById(Long id);

  @Query(
"""
  SELECT s FROM SecondaryItem s
  WHERE s.primaryItem.shopId.shopId = :shopId
  AND s.deletedAt IS NULL
""")
  List<SecondaryItem> findAllByShopId(@Param("shopId") Long shopId);

  List<SecondaryItem> findByPrimaryItemId(Long id);

  @Query(
"""
  SELECT s FROM SecondaryItem s
  WHERE s.primaryItem.shopId.shopId = :shopId
  AND s.deletedAt IS NULL
  AND s.isActive = true
""")
  List<SecondaryItem> findAllByShopIdAndIsActiveTrue(@Param("shopId") Long shopId);
}
