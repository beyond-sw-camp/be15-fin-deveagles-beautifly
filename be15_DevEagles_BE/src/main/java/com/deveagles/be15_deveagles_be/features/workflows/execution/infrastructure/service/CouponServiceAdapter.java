package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import com.deveagles.be15_deveagles_be.features.coupons.application.query.CouponQueryService;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponResponse;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponServiceAdapter {

  private final CouponQueryService couponQueryService;

  public boolean isValidCoupon(String couponCode, Long shopId) {
    try {
      if (couponCode == null || couponCode.trim().isEmpty()) {
        log.warn("쿠폰 코드가 비어 있습니다");
        return false;
      }

      Optional<CouponResponse> optCoupon = couponQueryService.getCouponByCode(couponCode);

      if (optCoupon.isEmpty()) {
        log.warn("쿠폰을 찾을 수 없습니다. couponCode: {}", couponCode);
        return false;
      }

      CouponResponse coupon = optCoupon.get();

      // 매장 사용 가능 여부
      if (!shopId.equals(coupon.getShopId())) {
        log.warn(
            "해당 매장에서 사용할 수 없는 쿠폰입니다. couponCode: {}, shopId: {}, coupon.shopId: {}",
            couponCode,
            shopId,
            coupon.getShopId());
        return false;
      }

      // 활성화 여부
      if (Boolean.FALSE.equals(coupon.getIsActive())) {
        log.warn("비활성화된 쿠폰입니다. couponCode: {}", couponCode);
        return false;
      }

      // 만료 여부
      if (Boolean.TRUE.equals(coupon.getIsExpired())) {
        log.warn("만료된 쿠폰입니다. couponCode: {}", couponCode);
        return false;
      }

      if (coupon.getExpirationDate() != null
          && coupon.getExpirationDate().isBefore(LocalDate.now())) {
        log.warn("만료일이 지난 쿠폰입니다. couponCode: {}", couponCode);
        return false;
      }

      log.info("쿠폰이 유효합니다. couponCode: {}, shopId: {}", couponCode, shopId);
      return true;

    } catch (Exception e) {
      log.error("쿠폰 검증 중 예외 발생. couponCode: {}, shopId: {}", couponCode, shopId, e);
      return false;
    }
  }
}
