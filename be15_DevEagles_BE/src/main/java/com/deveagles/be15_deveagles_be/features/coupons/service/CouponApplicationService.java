package com.deveagles.be15_deveagles_be.features.coupons.service;

import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponApplicationRequest;
import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponApplicationResult;
import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponValidationResult;
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

  public CouponValidationResult validateCoupon(CouponApplicationRequest request) {
    log.info("쿠폰 검증 요청 - 코드: {}", request.getCouponCode());
    return couponValidationService.validateForSale(request);
  }

  public CouponApplicationResult applyCoupon(CouponApplicationRequest request) {
    log.info("쿠폰 적용 요청 - 코드: {}, 원금액: {}", request.getCouponCode(), request.getOriginalAmount());

    CouponValidationResult validationResult = couponValidationService.validateForSale(request);

    if (!validationResult.isValid()) {
      return CouponApplicationResult.failed(validationResult.getErrorMessage());
    }

    return couponDiscountCalculator.calculateDiscount(
        validationResult.getCoupon(), request.getOriginalAmount());
  }
}
