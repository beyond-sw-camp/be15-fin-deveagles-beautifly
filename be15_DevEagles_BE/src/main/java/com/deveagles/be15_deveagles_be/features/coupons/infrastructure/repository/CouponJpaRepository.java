package com.deveagles.be15_deveagles_be.features.coupons.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

  Optional<Coupon> findByCouponCodeAndDeletedAtIsNull(String couponCode);

  Optional<Coupon> findByIdAndShopIdAndDeletedAtIsNull(Long id, Long shopId);

  Optional<Coupon> findByCouponCodeAndShopIdAndDeletedAtIsNull(String couponCode, Long shopId);

  @Query(
      "SELECT COUNT(c) > 0 FROM Coupon c WHERE c.couponCode = :couponCode AND c.deletedAt IS NULL")
  boolean existsByCouponCodeAndNotDeleted(@Param("couponCode") String couponCode);
}
