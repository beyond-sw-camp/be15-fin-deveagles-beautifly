package com.deveagles.be15_deveagles_be.features.coupons.application.query;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.coupons.common.CouponDto;
import com.deveagles.be15_deveagles_be.features.coupons.domain.repository.CouponQueryRepository;
import com.deveagles.be15_deveagles_be.features.coupons.infrastructure.repository.CouponJpaRepository;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CouponQueryServiceImpl implements CouponQueryService {

  private final CouponJpaRepository couponJpaRepository;
  private final CouponQueryRepository couponQueryRepository;

  @Override
  public Optional<CouponResponse> getCouponById(Long id, Long shopId) {
    log.info("쿠폰 ID로 조회 - ID: {}, 매장ID: {}", id, shopId);

    return couponJpaRepository
        .findByIdAndShopIdAndDeletedAtIsNull(id, shopId)
        .map(CouponResponse::from);
  }

  @Override
  public Optional<CouponResponse> getCouponByCode(String couponCode, Long shopId) {
    log.info("쿠폰 코드로 조회 - 코드: {}, 매장ID: {}", couponCode, shopId);

    return couponJpaRepository
        .findByCouponCodeAndShopIdAndDeletedAtIsNull(couponCode, shopId)
        .map(CouponResponse::from);
  }

  @Override
  public PagedResult<CouponResponse> searchCoupons(CouponSearchQuery query) {
    log.info(
        "쿠폰 검색 시작 - 조건: 매장ID={}, 활성상태={}, 직원ID={}, 상품ID={}",
        query.getShopId(),
        query.getIsActive(),
        query.getStaffId(),
        query.getPrimaryItemId());

    int page = query.getPage() != null ? query.getPage() : 0;
    int size = query.getSize() != null ? query.getSize() : 10;

    Pageable pageable = PageRequest.of(page, size);
    Page<CouponDto> coupons = couponQueryRepository.searchCoupons(query, pageable);

    Page<CouponResponse> pageResult = coupons.map(CouponResponse::from);

    log.info("쿠폰 검색 완료 - 총 {}개, 현재 페이지: {}", coupons.getTotalElements(), page);

    return PagedResult.from(pageResult);
  }
}
