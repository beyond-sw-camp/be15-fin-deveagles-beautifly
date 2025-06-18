package com.deveagles.be15_deveagles_be.features.coupons.command.service;

import com.deveagles.be15_deveagles_be.features.coupons.command.dto.CreateCouponCommand;
import com.deveagles.be15_deveagles_be.features.coupons.command.dto.DeleteCouponCommand;
import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponDto;

public interface CouponCommandService {

  CouponDto createCoupon(CreateCouponCommand command);

  void deleteCoupon(DeleteCouponCommand command);

  CouponDto toggleCouponStatus(Long couponId);
}
