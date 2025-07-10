package com.deveagles.be15_deveagles_be.features.coupons.application.query;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponResponse;
import java.util.Optional;

public interface CouponQueryService {

  Optional<CouponResponse> getCouponById(Long id, Long shopId);

  Optional<CouponResponse> getCouponByCode(String couponCode, Long shopId);

  PagedResult<CouponResponse> searchCoupons(CouponSearchQuery query);
}
