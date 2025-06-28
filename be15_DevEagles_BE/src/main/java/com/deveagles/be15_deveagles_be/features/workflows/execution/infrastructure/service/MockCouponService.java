package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// TODO: 나중에 지울 파일임

@Service
@Slf4j
public class MockCouponService implements ActionExecutorServiceImpl.CouponService {

  @Override
  public boolean isValidCoupon(String couponId, Long shopId) {
    log.info("Mock 쿠폰 유효성 검증: 쿠폰ID={}, 매장ID={}", couponId, shopId);

    // 임시로 기본적인 유효성 검증 (null이 아니고 빈 문자열이 아니면 유효)
    boolean isValid = couponId != null && !couponId.trim().isEmpty() && shopId != null;

    log.info("쿠폰 유효성 결과: {}", isValid);
    return isValid;
  }
}
