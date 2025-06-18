package com.deveagles.be15_deveagles_be.features.coupons.dto;

import com.deveagles.be15_deveagles_be.features.coupons.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CouponValidationResult {

  private final boolean valid;
  private final String errorMessage;
  private final Coupon coupon;

  public static CouponValidationResult valid(Coupon coupon) {
    return new CouponValidationResult(true, null, coupon);
  }

  public static CouponValidationResult invalid(String errorMessage) {
    return new CouponValidationResult(false, errorMessage, null);
  }
}
