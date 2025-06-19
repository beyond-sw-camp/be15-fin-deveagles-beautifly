package com.deveagles.be15_deveagles_be.features.coupons.service;

import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponApplicationResult;
import com.deveagles.be15_deveagles_be.features.coupons.entity.Coupon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CouponDiscountCalculator {

  public CouponApplicationResult calculateDiscount(Coupon coupon, Integer originalAmount) {
    log.info(
        "할인 계산 시작 - 쿠폰ID: {}, 원금액: {}, 할인율: {}%",
        coupon.getId(), originalAmount, coupon.getDiscountRate());

    if (originalAmount == null || originalAmount <= 0) {
      return CouponApplicationResult.failed("유효하지 않은 금액입니다");
    }

    Integer discountRate = coupon.getDiscountRate();
    Integer discountAmount = calculateDiscountAmount(originalAmount, discountRate);
    Integer finalAmount = originalAmount - discountAmount;

    log.info("할인 계산 완료 - 할인율: {}%, 할인금액: {}, 최종금액: {}", discountRate, discountAmount, finalAmount);

    return CouponApplicationResult.success(coupon, discountRate, discountAmount, finalAmount);
  }

  private Integer calculateDiscountAmount(Integer originalAmount, Integer discountRate) {
    if (originalAmount == null || discountRate == null) {
      return 0;
    }
    return originalAmount * discountRate / 100;
  }
}
