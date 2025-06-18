package com.deveagles.be15_deveagles_be.features.coupons.query.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.coupons.query.dto.CouponQuery;
import com.deveagles.be15_deveagles_be.features.coupons.query.dto.CouponResponse;
import java.util.Optional;

public interface CouponQueryService {

  Optional<CouponResponse> getCouponById(Long id);

  Optional<CouponResponse> getCouponByCode(String couponCode);

  PagedResult<CouponResponse> searchCoupons(CouponQuery query);
}
