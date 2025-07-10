package com.deveagles.be15_deveagles_be.features.coupons.infrastructure.repository;

import static com.deveagles.be15_deveagles_be.features.coupons.domain.entity.QCoupon.coupon;
import static com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.QPrimaryItem.primaryItem;
import static com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.QSecondaryItem.secondaryItem;
import static com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.QStaff.staff;

import com.deveagles.be15_deveagles_be.features.coupons.application.query.CouponSearchQuery;
import com.deveagles.be15_deveagles_be.features.coupons.common.CouponDto;
import com.deveagles.be15_deveagles_be.features.coupons.domain.repository.CouponQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<CouponDto> searchCoupons(CouponSearchQuery query, Pageable pageable) {
    log.info(
        "쿠폰 검색 시작 - 조건: 매장ID={}, 활성상태={}, 직원ID={}, 상품ID={}",
        query.getShopId(),
        query.getIsActive(),
        query.getStaffId(),
        query.getPrimaryItemId());

    BooleanBuilder builder = new BooleanBuilder();
    builder.and(coupon.deletedAt.isNull());

    // Add search conditions
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

    Long totalCount = queryFactory.select(coupon.count()).from(coupon).where(builder).fetchOne();

    log.info("쿠폰 총 개수: {}", totalCount);

    OrderSpecifier<?> orderSpecifier = getOrderSpecifier(query);

    List<CouponDto> coupons =
        queryFactory
            .select(
                Projections.constructor(
                    CouponDto.class,
                    coupon.id,
                    coupon.couponCode,
                    coupon.couponTitle,
                    coupon.shopId,
                    coupon.discountRate,
                    coupon.expirationDate,
                    coupon.isActive,
                    coupon.createdAt,
                    staff.staffId,
                    staff.staffName,
                    primaryItem.primaryItemId,
                    primaryItem.primaryItemName,
                    primaryItem.category.stringValue(),
                    secondaryItem.secondaryItemId.longValue(),
                    secondaryItem.secondaryItemName.stringValue()))
            .from(coupon)
            .leftJoin(staff)
            .on(coupon.staffId.eq(staff.staffId))
            .leftJoin(primaryItem)
            .on(coupon.primaryItemId.eq(primaryItem.primaryItemId))
            .leftJoin(secondaryItem)
            .on(coupon.secondaryItemId.eq(secondaryItem.secondaryItemId))
            .where(builder)
            .orderBy(orderSpecifier)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    log.info(
        "쿠폰 검색 완료 - 조회된 건수: {}, 총 개수: {}, 현재 페이지: {}",
        coupons.size(),
        totalCount,
        pageable.getPageNumber());

    return new PageImpl<>(coupons, pageable, totalCount != null ? totalCount : 0);
  }

  private OrderSpecifier<?> getOrderSpecifier(CouponSearchQuery query) {
    String sortBy = query.getSortBy() != null ? query.getSortBy() : "createdAt";
    String sortDirection = query.getSortDirection() != null ? query.getSortDirection() : "desc";

    OrderSpecifier<?> orderSpecifier;

    switch (sortBy.toLowerCase()) {
      case "createdat":
      case "created_at":
        orderSpecifier =
            "asc".equalsIgnoreCase(sortDirection)
                ? coupon.createdAt.asc()
                : coupon.createdAt.desc();
        break;
      case "couponcode":
      case "coupon_code":
        orderSpecifier =
            "asc".equalsIgnoreCase(sortDirection)
                ? coupon.couponCode.asc()
                : coupon.couponCode.desc();
        break;
      case "coupontitle":
      case "coupon_title":
        orderSpecifier =
            "asc".equalsIgnoreCase(sortDirection)
                ? coupon.couponTitle.asc()
                : coupon.couponTitle.desc();
        break;
      case "expirationdate":
      case "expiration_date":
        orderSpecifier =
            "asc".equalsIgnoreCase(sortDirection)
                ? coupon.expirationDate.asc()
                : coupon.expirationDate.desc();
        break;
      default:
        orderSpecifier = coupon.createdAt.desc();
        break;
    }

    return orderSpecifier;
  }
}
