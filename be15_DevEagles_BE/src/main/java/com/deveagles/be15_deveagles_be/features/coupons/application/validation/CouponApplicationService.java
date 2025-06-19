package com.deveagles.be15_deveagles_be.features.coupons.application.validation;

import com.deveagles.be15_deveagles_be.features.coupons.common.CouponResponseFactory;
import com.deveagles.be15_deveagles_be.features.coupons.domain.service.CouponDiscountCalculator;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.request.CouponApplicationRequest;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponApplicationResponse;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponValidationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CouponApplicationService {

  private final CouponValidationService couponValidationService;
  private final CouponDiscountCalculator couponDiscountCalculator;
  private final CouponResponseFactory couponResponseFactory;

  public CouponValidationResponse validateCoupon(CouponApplicationRequest request) {
    log.info("쿠폰 검증 요청 - 코드: {}", request.getCouponCode());
    return couponValidationService.validateForSale(request);
  }

  public CouponApplicationResponse applyCoupon(CouponApplicationRequest request) {
    log.info("쿠폰 적용 요청 - 코드: {}, 원금액: {}", request.getCouponCode(), request.getOriginalAmount());

    CouponValidationResponse validationResult = couponValidationService.validateForSale(request);

    if (!validationResult.isValid()) {
      return couponResponseFactory.createFailedResponse(validationResult.getErrorMessage());
    }

    return couponDiscountCalculator.calculateDiscount(
        validationResult.getCoupon(), request.getOriginalAmount());
  }
}
