package com.deveagles.be15_deveagles_be.features.coupons.domain.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.domain.vo.DiscountResult;

public class CouponDiscountCalculator {

  public DiscountResult calculateDiscount(Coupon coupon, Integer originalAmount) {
    validateInput(coupon, originalAmount);

    Integer discountRate = coupon.getDiscountRate();
    Integer discountAmount = calculateDiscountAmount(originalAmount, discountRate);
    Integer finalAmount = originalAmount - discountAmount;

    return DiscountResult.of(originalAmount, discountRate, discountAmount, finalAmount);
  }

  private void validateInput(Coupon coupon, Integer originalAmount) {
    if (coupon == null) {
      throw new BusinessException(ErrorCode.VALIDATION_ERROR, "쿠폰이 null입니다");
    }
    if (originalAmount == null || originalAmount <= 0) {
      throw new BusinessException(ErrorCode.COUPON_INVALID_AMOUNT);
    }
    if (coupon.getDiscountRate() == null
        || coupon.getDiscountRate() < 0
        || coupon.getDiscountRate() > 100) {
      throw new BusinessException(ErrorCode.COUPON_INVALID_DISCOUNT_RATE);
    }
  }

  private Integer calculateDiscountAmount(Integer originalAmount, Integer discountRate) {
    return originalAmount * discountRate / 100;
  }
}
