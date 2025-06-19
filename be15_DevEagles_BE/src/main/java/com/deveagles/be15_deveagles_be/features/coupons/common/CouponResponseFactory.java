package com.deveagles.be15_deveagles_be.features.coupons.common;

import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponApplicationResponse;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponValidationResponse;

public interface CouponResponseFactory {

  CouponValidationResponse createValidResponse(Coupon coupon);

  CouponValidationResponse createInvalidResponse(String errorMessage);

  CouponApplicationResponse createSuccessResponse(
      Coupon coupon, Integer discountRate, Integer discountAmount, Integer finalAmount);

  CouponApplicationResponse createFailedResponse(String errorMessage);
}
