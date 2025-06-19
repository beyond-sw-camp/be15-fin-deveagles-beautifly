package com.deveagles.be15_deveagles_be.features.coupons.domain.repository;

import com.deveagles.be15_deveagles_be.features.coupons.application.query.CouponSearchQuery;
import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponQueryRepository {

  Page<Coupon> searchCoupons(CouponSearchQuery query, Pageable pageable);
}
