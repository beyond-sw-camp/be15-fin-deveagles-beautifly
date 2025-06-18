package com.deveagles.be15_deveagles_be.features.coupons.service;

import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponApplicationRequest;
import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponValidationResult;
import com.deveagles.be15_deveagles_be.features.coupons.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.repository.CouponRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CouponValidationService {

  private final CouponRepository couponRepository;

  public CouponValidationResult validateForSale(CouponApplicationRequest request) {
    log.info("쿠폰 검증 시작 - 코드: {}, 매장ID: {}", request.getCouponCode(), request.getShopId());

    Optional<Coupon> couponOpt =
        couponRepository.findByCouponCodeAndDeletedAtIsNull(request.getCouponCode());

    if (couponOpt.isEmpty()) {
      return CouponValidationResult.invalid("쿠폰을 찾을 수 없습니다");
    }

    Coupon coupon = couponOpt.get();

    if (!coupon.getShopId().equals(request.getShopId())) {
      return CouponValidationResult.invalid("해당 매장에서 사용할 수 없는 쿠폰입니다");
    }

    if (!coupon.getIsActive()) {
      return CouponValidationResult.invalid("비활성화된 쿠폰입니다");
    }

    if (coupon.isExpired()) {
      return CouponValidationResult.invalid("만료된 쿠폰입니다");
    }

    if (!isStaffValid(coupon, request.getStaffId())) {
      return CouponValidationResult.invalid("해당 직원이 사용할 수 없는 쿠폰입니다");
    }

    if (!isItemValid(coupon, request.getPrimaryItemId(), request.getSecondaryItemId())) {
      return CouponValidationResult.invalid("해당 상품에 사용할 수 없는 쿠폰입니다");
    }

    log.info("쿠폰 검증 성공 - 쿠폰ID: {}, 할인율: {}%", coupon.getId(), coupon.getDiscountRate());
    return CouponValidationResult.valid(coupon);
  }

  private boolean isStaffValid(Coupon coupon, Long requestStaffId) {
    if (coupon.getStaffId() == null) {
      log.debug("쿠폰 직원 제한 없음 - 모든 직원 사용 가능");
      return true;
    }

    boolean isValid = coupon.getStaffId().equals(requestStaffId);
    log.debug(
        "쿠폰 직원 검증 - 쿠폰직원ID: {}, 요청직원ID: {}, 결과: {}", coupon.getStaffId(), requestStaffId, isValid);
    return isValid;
  }

  private boolean isItemValid(
      Coupon coupon, Long requestPrimaryItemId, Long requestSecondaryItemId) {
    if (!coupon.getPrimaryItemId().equals(requestPrimaryItemId)) {
      log.debug(
          "1차 상품 불일치 - 쿠폰1차상품ID: {}, 요청1차상품ID: {}",
          coupon.getPrimaryItemId(),
          requestPrimaryItemId);
      return false;
    }

    if (coupon.getSecondaryItemId() == null) {
      log.debug("쿠폰 2차 상품 제한 없음 - 1차 상품 전체 적용 가능");
      return true;
    }

    boolean isValid = coupon.getSecondaryItemId().equals(requestSecondaryItemId);
    log.debug(
        "2차 상품 검증 - 쿠폰2차상품ID: {}, 요청2차상품ID: {}, 결과: {}",
        coupon.getSecondaryItemId(),
        requestSecondaryItemId,
        isValid);
    return isValid;
  }
}
