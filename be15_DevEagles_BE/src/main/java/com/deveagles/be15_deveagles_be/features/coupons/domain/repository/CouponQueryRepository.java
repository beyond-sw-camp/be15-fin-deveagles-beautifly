package com.deveagles.be15_deveagles_be.features.coupons.domain.repository;

import com.deveagles.be15_deveagles_be.features.coupons.application.query.CouponSearchQuery;
import com.deveagles.be15_deveagles_be.features.coupons.common.CouponDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponQueryRepository {

  Page<CouponDto> searchCoupons(CouponSearchQuery query, Pageable pageable);
}
