package com.deveagles.be15_deveagles_be.features.staffsales.command.repository;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.Incentive;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IncentiveRepository extends JpaRepository<Incentive, Long> {

  @Query(
      """
    SELECT i
     FROM Incentive i
     JOIN Shop s ON s.shopId = i.shopId
    WHERE i.shopId = :shopId
      AND i.type = :type
      AND i.isActive = true
      AND (i.staffId IS NULL OR i.staffId = :staffId)
      AND (s.incentiveStatus = true)
   """)
  List<Incentive> findActiveIncentivesByShopIdAndType(
      @Param("shopId") Long shopId,
      @Param("type") ProductType type,
      @Param("staffId") Long staffId);

  List<Incentive> findByShopId(Long shopId);

  List<Incentive> findAllByShopId(Long shopId);

  @Query(
      """
    SELECT i FROM Incentive i
    WHERE i.shopId = :shopId
      AND i.staffId = :staffId
  """)
  List<Incentive> findAllByShopIdAndStaffId(
      @Param("shopId") Long shopId, @Param("staffId") Long staffId);

  @Query(
      """
    SELECT i FROM Incentive i
    WHERE i.shopId = :shopId
      AND (
        (:staffId IS NULL AND i.staffId IS NULL)
        OR (:staffId IS NOT NULL AND i.staffId = :staffId)
      )
      AND i.paymentsMethod = :method
      AND i.type = :type
   """)
  Optional<Incentive> findByShopIdAndStaffIdAndPaymentsMethodAndType(
      @Param("shopId") Long shopId,
      @Param("staffId") Long staffId,
      @Param("method") PaymentsMethod method,
      @Param("type") ProductType type);

  @Query(
      """
        SELECT i
         FROM Incentive i
        WHERE i.shopId = :shopId
          AND i.isActive = true
       """)
  List<Incentive> findByShopIdAndIsActiveTrue(Long shopId);

  @Query(
"""
    SELECT i
    FROM Incentive i
    WHERE i.shopId = :shopId
      AND i.paymentsMethod = :paymentsMethod
      AND i.type = :type
      AND i.staffId IS NOT NULL
      AND i.isActive = true
""")
  List<Incentive> findStaffSpecificIncentives(
      @Param("shopId") Long shopId,
      @Param("paymentsMethod") PaymentsMethod paymentsMethod,
      @Param("type") ProductType type);

  @Query(
"""
    SELECT i
    FROM Incentive i
    WHERE i.shopId = :shopId
      AND i.paymentsMethod = :paymentsMethod
      AND i.type = :type
      AND i.staffId IS NULL
      AND i.isActive = true
""")
  Optional<Incentive> findCommonIncentives(
      @Param("shopId") Long shopId,
      @Param("paymentsMethod") PaymentsMethod paymentsMethod,
      @Param("type") ProductType type);
}
