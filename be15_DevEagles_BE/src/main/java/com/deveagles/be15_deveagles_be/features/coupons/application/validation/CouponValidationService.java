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

    if (!coupon.isUsableForShop(request.getShopId())) {
      return couponResponseFactory.createInvalidResponse("해당 매장에서 사용할 수 없는 쿠폰입니다");
    }

    if (!coupon.getIsActive()) {
      return couponResponseFactory.createInvalidResponse("비활성화된 쿠폰입니다");
    }

    if (coupon.isExpired()) {
      return couponResponseFactory.createInvalidResponse("만료된 쿠폰입니다");
    }

    if (!coupon.isUsableForStaff(request.getStaffId())) {
      return couponResponseFactory.createInvalidResponse("해당 직원에 적용할 수 없는 쿠폰입니다");
    }

    if (!coupon.isUsableForItem(request.getPrimaryItemId(), request.getSecondaryItemId())) {
      return couponResponseFactory.createInvalidResponse("해당 상품에 적용할 수 없는 쿠폰입니다");
    }

    log.info("쿠폰 검증 성공 - 쿠폰ID: {}, 할인율: {}%", coupon.getId(), coupon.getDiscountRate());
    return couponResponseFactory.createValidResponse(coupon);
  }
}
