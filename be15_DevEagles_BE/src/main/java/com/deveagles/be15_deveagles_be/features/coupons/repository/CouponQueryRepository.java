package com.deveagles.be15_deveagles_be.features.coupons.repository;

import com.deveagles.be15_deveagles_be.features.coupons.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.query.dto.CouponQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponQueryRepository {

  Page<Coupon> searchCoupons(CouponQuery query, Pageable pageable);
}
