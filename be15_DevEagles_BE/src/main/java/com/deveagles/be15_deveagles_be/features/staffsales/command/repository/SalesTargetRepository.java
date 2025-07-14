package com.deveagles.be15_deveagles_be.features.staffsales.command.repository;

import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.SalesTarget;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesTargetRepository extends JpaRepository<SalesTarget, Long> {
  @Query(
      """
        SELECT s FROM SalesTarget s
         WHERE s.shopId = :shopId
           AND s.targetYear = :targetYear
           AND s.targetMonth = :targetMonth
    """)
  List<SalesTarget> findByShopIdAndYearMonth(
      @Param("shopId") Long shopId, @Param("targetYear") int year, @Param("targetMonth") int month);
}
