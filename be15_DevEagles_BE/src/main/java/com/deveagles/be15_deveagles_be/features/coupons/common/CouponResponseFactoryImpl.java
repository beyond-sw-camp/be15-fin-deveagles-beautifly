package com.deveagles.be15_deveagles_be.features.coupons.common;

import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponApplicationResponse;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponValidationResponse;
import org.springframework.stereotype.Service;

@Service
public class CouponResponseFactoryImpl implements CouponResponseFactory {

  @Override
  public CouponValidationResponse createValidResponse(Coupon coupon) {
    return new CouponValidationResponse(true, null, coupon);
  }

  @Override
  public CouponValidationResponse createInvalidResponse(String errorMessage) {
    return new CouponValidationResponse(false, errorMessage, null);
  }

  @Override
  public CouponApplicationResponse createSuccessResponse(
      Coupon coupon, Integer discountRate, Integer discountAmount, Integer finalAmount) {
    return new CouponApplicationResponse(
        true, null, coupon, discountRate, discountAmount, finalAmount);
  }

  @Override
  public CouponApplicationResponse createFailedResponse(String errorMessage) {
    return new CouponApplicationResponse(false, errorMessage, null, null, null, null);
  }
}
