package com.deveagles.be15_deveagles_be.features.coupons.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiscountResult {

  private final Integer originalAmount;
  private final Integer discountRate;
  private final Integer discountAmount;
  private final Integer finalAmount;

  public static DiscountResult of(
      Integer originalAmount, Integer discountRate, Integer discountAmount, Integer finalAmount) {
    return new DiscountResult(originalAmount, discountRate, discountAmount, finalAmount);
  }

  public boolean isValid() {
    return originalAmount != null
        && originalAmount > 0
        && discountRate != null
        && discountRate >= 0
        && discountAmount != null
        && discountAmount >= 0
        && finalAmount != null
        && finalAmount >= 0;
  }
}
