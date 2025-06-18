package com.deveagles.be15_deveagles_be.features.coupons.command.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.coupons.command.dto.CreateCouponCommand;
import com.deveagles.be15_deveagles_be.features.coupons.command.dto.DeleteCouponCommand;
import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponDto;
import com.deveagles.be15_deveagles_be.features.coupons.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.repository.CouponRepository;
import com.deveagles.be15_deveagles_be.features.coupons.utils.CouponCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CouponCommandServiceImpl implements CouponCommandService {

  private final CouponRepository couponRepository;
  private final CouponCodeGenerator couponCodeGenerator;

  @Override
  public CouponDto createCoupon(CreateCouponCommand command) {
    log.info("쿠폰 생성 시작 - 쿠폰명: {}", command.getCouponTitle());

    String couponCode = generateUniqueCouponCode();

    Coupon coupon =
        Coupon.builder()
            .couponCode(couponCode)
            .couponTitle(command.getCouponTitle())
            .shopId(command.getShopId())
            .staffId(command.getStaffId())
            .primaryItemId(command.getPrimaryItemId())
            .secondaryItemId(command.getSecondaryItemId())
            .discountRate(command.getDiscountRate())
            .expirationDate(command.getExpirationDate())
            .isActive(command.getIsActive())
            .build();

    Coupon savedCoupon = couponRepository.save(coupon);
    log.info("쿠폰 생성 완료 - ID: {}, 쿠폰코드: {}", savedCoupon.getId(), savedCoupon.getCouponCode());

    return CouponDto.from(savedCoupon);
  }

  private String generateUniqueCouponCode() {
    String couponCode;
    int attempts = 0;
    final int maxAttempts = 10;

    do {
      couponCode = couponCodeGenerator.generateCouponCode();
      attempts++;

      if (attempts > maxAttempts) {
        log.error("쿠폰 코드 생성 실패 - 최대 시도 횟수 초과");
        throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "쿠폰 코드 생성에 실패했습니다");
      }
    } while (couponRepository.existsByCouponCodeAndNotDeleted(couponCode));

    if (attempts > 1) {
      log.info("쿠폰 코드 생성 - {}번 시도 후 성공: {}", attempts, couponCode);
    }

    return couponCode;
  }

  @Override
  public void deleteCoupon(DeleteCouponCommand command) {
    log.info("쿠폰 삭제 시작 - ID: {}", command.getId());

    Coupon coupon =
        couponRepository
            .findById(command.getId())
            .orElseThrow(
                () -> {
                  log.warn("쿠폰을 찾을 수 없음 - ID: {}", command.getId());
                  return new BusinessException(ErrorCode.COUPON_NOT_FOUND);
                });

    if (coupon.isDeleted()) {
      log.warn("이미 삭제된 쿠폰 - ID: {}", command.getId());
      throw new BusinessException(ErrorCode.COUPON_ALREADY_DELETED);
    }

    coupon.softDelete();
    couponRepository.save(coupon);
    log.info("쿠폰 삭제 완료 - ID: {}", command.getId());
  }

  @Override
  public CouponDto toggleCouponStatus(Long couponId) {
    log.info("쿠폰 상태 토글 시작 - ID: {}", couponId);

    Coupon coupon =
        couponRepository
            .findById(couponId)
            .orElseThrow(
                () -> {
                  log.warn("쿠폰을 찾을 수 없음 - ID: {}", couponId);
                  return new BusinessException(ErrorCode.COUPON_NOT_FOUND);
                });

    if (coupon.isDeleted()) {
      log.warn("삭제된 쿠폰 상태 변경 시도 - ID: {}", couponId);
      throw new BusinessException(ErrorCode.DELETED_COUPON_OPERATION_NOT_ALLOWED);
    }

    if (coupon.getIsActive()) {
      coupon.deactivate();
      log.info("쿠폰 비활성화 완료 - ID: {}", couponId);
    } else {
      coupon.activate();
      log.info("쿠폰 활성화 완료 - ID: {}", couponId);
    }

    Coupon savedCoupon = couponRepository.save(coupon);
    return CouponDto.from(savedCoupon);
  }
}
