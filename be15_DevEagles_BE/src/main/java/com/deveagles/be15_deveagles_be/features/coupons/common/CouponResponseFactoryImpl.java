package com.deveagles.be15_deveagles_be.features.coupons.common;

import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.domain.vo.DiscountResult;
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
      Coupon coupon, DiscountResult discountResult) {
    return new CouponApplicationResponse(
        true,
        null,
        coupon,
        discountResult.getDiscountRate(),
        discountResult.getDiscountAmount(),
        discountResult.getFinalAmount());
  }

  @Override
  public CouponApplicationResponse createFailedResponse(String errorMessage) {
    return new CouponApplicationResponse(false, errorMessage, null, null, null, null);
  }
}
