package com.deveagles.be15_deveagles_be.features.coupons.application.validation;

import com.deveagles.be15_deveagles_be.features.coupons.common.CouponResponseFactory;
import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.infrastructure.repository.CouponJpaRepository;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.request.CouponApplicationRequest;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponValidationResponse;
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

  private final CouponJpaRepository couponJpaRepository;
  private final CouponResponseFactory couponResponseFactory;

  public CouponValidationResponse validateForSale(CouponApplicationRequest request) {
    log.info("쿠폰 검증 시작 - 코드: {}, 매장ID: {}", request.getCouponCode(), request.getShopId());

    Optional<Coupon> couponOpt =
        couponJpaRepository.findByCouponCodeAndDeletedAtIsNull(request.getCouponCode());

    if (couponOpt.isEmpty()) {
      return couponResponseFactory.createInvalidResponse("쿠폰을 찾을 수 없습니다");
    }

    Coupon coupon = couponOpt.get();

    if (!coupon.getShopId().equals(request.getShopId())) {
      return couponResponseFactory.createInvalidResponse("해당 매장에서 사용할 수 없는 쿠폰입니다");
    }

    if (!coupon.getIsActive()) {
      return couponResponseFactory.createInvalidResponse("비활성화된 쿠폰입니다");
    }

    if (coupon.isExpired()) {
      return couponResponseFactory.createInvalidResponse("만료된 쿠폰입니다");
    }

    if (!isStaffValid(coupon, request.getStaffId())) {
      return couponResponseFactory.createInvalidResponse("해당 직원에 적용할 수 없는 쿠폰입니다");
    }

    if (!isItemValid(coupon, request.getPrimaryItemId(), request.getSecondaryItemId())) {
      return couponResponseFactory.createInvalidResponse("해당 상품에 적용할 수 없는 쿠폰입니다");
    }

    log.info("쿠폰 검증 성공 - 쿠폰ID: {}, 할인율: {}%", coupon.getId(), coupon.getDiscountRate());
    return couponResponseFactory.createValidResponse(coupon);
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
