package com.deveagles.be15_deveagles_be.features.coupons.infrastructure.repository;

import static com.deveagles.be15_deveagles_be.features.coupons.domain.entity.QCoupon.coupon;

import com.deveagles.be15_deveagles_be.features.coupons.application.query.CouponSearchQuery;
import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.domain.repository.CouponQueryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CouponQueryRepositoryImpl implements CouponQueryRepository {

  private final com.querydsl.jpa.impl.JPAQueryFactory queryFactory;

  @Override
  public Page<Coupon> searchCoupons(CouponSearchQuery query, Pageable pageable) {
    log.info(
        "쿠폰 검색 시작 - 조건: 매장ID={}, 활성상태={}, 직원ID={}, 상품ID={}",
        query.getShopId(),
        query.getIsActive(),
        query.getStaffId(),
        query.getPrimaryItemId());

    com.querydsl.core.BooleanBuilder builder = new com.querydsl.core.BooleanBuilder();

    builder.and(coupon.deletedAt.isNull());

    if (query.getCouponCode() != null && !query.getCouponCode().trim().isEmpty()) {
      builder.and(coupon.couponCode.containsIgnoreCase(query.getCouponCode()));
    }

    if (query.getCouponTitle() != null && !query.getCouponTitle().trim().isEmpty()) {
      builder.and(coupon.couponTitle.containsIgnoreCase(query.getCouponTitle()));
    }

    if (query.getShopId() != null) {
      builder.and(coupon.shopId.eq(query.getShopId()));
    }

    if (query.getStaffId() != null) {
      builder.and(coupon.staffId.eq(query.getStaffId()));
    }

    if (query.getPrimaryItemId() != null) {
      builder.and(coupon.primaryItemId.eq(query.getPrimaryItemId()));
    }

    if (query.getIsActive() != null) {
      builder.and(coupon.isActive.eq(query.getIsActive()));
    }

    if (query.getExpirationDateFrom() != null) {
      builder.and(coupon.expirationDate.goe(query.getExpirationDateFrom()));
    }
    if (query.getExpirationDateTo() != null) {
      builder.and(coupon.expirationDate.loe(query.getExpirationDateTo()));
    }

    com.querydsl.core.types.OrderSpecifier<?> orderSpecifier = getOrderSpecifier(query);

    Long totalCount = queryFactory.select(coupon.count()).from(coupon).where(builder).fetchOne();

    List<Coupon> coupons =
        queryFactory
            .selectFrom(coupon)
            .where(builder)
            .orderBy(orderSpecifier)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    log.info("쿠폰 검색 완료 - 총 {}개, 현재 페이지: {}", totalCount, pageable.getPageNumber());

    return new PageImpl<>(coupons, pageable, totalCount != null ? totalCount : 0);
  }

  private com.querydsl.core.types.OrderSpecifier<?> getOrderSpecifier(CouponSearchQuery query) {
    String sortBy = query.getSortBy() != null ? query.getSortBy() : "createdAt";
    String sortDirection = query.getSortDirection() != null ? query.getSortDirection() : "desc";

    com.querydsl.core.types.dsl.PathBuilder<Coupon> pathBuilder =
        new com.querydsl.core.types.dsl.PathBuilder<>(Coupon.class, "coupon");

    if ("asc".equalsIgnoreCase(sortDirection)) {
      return pathBuilder.getString(sortBy).asc();
    } else {
      return pathBuilder.getString(sortBy).desc();
    }
  }
}
