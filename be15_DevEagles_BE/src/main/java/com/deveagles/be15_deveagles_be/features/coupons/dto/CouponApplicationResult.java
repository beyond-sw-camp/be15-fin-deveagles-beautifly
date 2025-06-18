package com.deveagles.be15_deveagles_be.features.coupons.dto;

import com.deveagles.be15_deveagles_be.features.coupons.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponApplicationResult {

  private final boolean success;
  private final String errorMessage;
  private final Coupon coupon;
  private final Integer discountRate;
  private final Integer discountAmount;
  private final Integer finalAmount;

  public static CouponApplicationResult success(
      Coupon coupon, Integer discountRate, Integer discountAmount, Integer finalAmount) {
    return new CouponApplicationResult(
        true, null, coupon, discountRate, discountAmount, finalAmount);
  }

  public static CouponApplicationResult failed(String errorMessage) {
    return new CouponApplicationResult(false, errorMessage, null, null, null, null);
  }
}
