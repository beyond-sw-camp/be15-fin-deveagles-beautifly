package com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response;

import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationResponse {

  private boolean valid;
  private String errorMessage;
  private Coupon coupon;
}
