package com.deveagles.be15_deveagles_be.features.coupons.repository;

import com.deveagles.be15_deveagles_be.features.coupons.entity.Coupon;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

  // 활성 쿠폰 조회
  @Query("SELECT c FROM Coupon c WHERE c.deletedAt IS NULL AND c.isActive = true")
  List<Coupon> findAllActiveCoupons();

  // 쿠폰 코드로 조회
  Optional<Coupon> findByCouponCodeAndDeletedAtIsNull(String couponCode);

  // 삭제되지 않은 쿠폰 조회
  @Query("SELECT c FROM Coupon c WHERE c.deletedAt IS NULL")
  Page<Coupon> findAllNotDeleted(Pageable pageable);

  // 직원별 쿠폰 조회
  @Query("SELECT c FROM Coupon c WHERE c.staffId = :staffId AND c.deletedAt IS NULL")
  List<Coupon> findByStaffIdAndNotDeleted(@Param("staffId") Long staffId);

  // 상품별 쿠폰 조회
  @Query("SELECT c FROM Coupon c WHERE c.primaryItemId = :itemId AND c.deletedAt IS NULL")
  List<Coupon> findByPrimaryItemIdAndNotDeleted(@Param("itemId") Long itemId);

  // 만료 예정 쿠폰 조회
  @Query(
      "SELECT c FROM Coupon c WHERE c.expirationDate <= :date AND c.deletedAt IS NULL AND c.isActive = true")
  List<Coupon> findExpiringCoupons(@Param("date") LocalDate date);

  // 쿠폰 코드 존재 여부 확인
  @Query(
      "SELECT COUNT(c) > 0 FROM Coupon c WHERE c.couponCode = :couponCode AND c.deletedAt IS NULL")
  boolean existsByCouponCodeAndNotDeleted(@Param("couponCode") String couponCode);

  // 할인율 범위로 쿠폰 조회
  @Query(
      "SELECT c FROM Coupon c WHERE c.discountRate BETWEEN :minRate AND :maxRate AND c.deletedAt IS NULL")
  List<Coupon> findByDiscountRateBetweenAndNotDeleted(
      @Param("minRate") Integer minRate, @Param("maxRate") Integer maxRate);
}
