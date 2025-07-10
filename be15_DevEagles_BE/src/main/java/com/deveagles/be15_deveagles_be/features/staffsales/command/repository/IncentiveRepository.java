package com.deveagles.be15_deveagles_be.features.staffsales.command.repository;

import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.Incentive;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IncentiveRepository extends JpaRepository<Incentive, Long> {

  @Query(
      """
    SELECT i FROM Incentive i
    WHERE i.shopId = :shopId
      AND i.type = :type
      AND i.isActive = true
      AND (i.staffId IS NULL OR i.staffId = :staffId)
    """)
  List<Incentive> findActiveIncentivesByShopIdAndType(
      @Param("shopId") Long shopId,
      @Param("type") ProductType type,
      @Param("staffId") Long staffId);

  List<Incentive> findByShopId(Long shopId);
}
