package com.deveagles.be15_deveagles_be.features.coupons.application.command;

import com.deveagles.be15_deveagles_be.features.coupons.common.CouponDto;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.request.DeleteCouponRequest;

public interface CouponCommandService {

  CouponDto createCoupon(CreateCouponRequest command);

  void deleteCoupon(DeleteCouponRequest command);

  CouponDto toggleCouponStatus(Long couponId, Long shopId);
}
