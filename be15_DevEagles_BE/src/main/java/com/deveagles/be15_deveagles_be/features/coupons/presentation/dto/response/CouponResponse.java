package com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response;

import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponse {

  private Long id;
  private String couponCode;
  private String couponTitle;
  private Long shopId;
  private Long staffId;
  private Long primaryItemId;
  private Long secondaryItemId;
  private Integer discountRate;
  private LocalDate expirationDate;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private Boolean isExpired;
  private Boolean isDeleted;

  public static CouponResponse from(Coupon coupon) {
    return CouponResponse.builder()
        .id(coupon.getId())
        .couponCode(coupon.getCouponCode())
        .couponTitle(coupon.getCouponTitle())
        .shopId(coupon.getShopId())
        .staffId(coupon.getStaffId())
        .primaryItemId(coupon.getPrimaryItemId())
        .secondaryItemId(coupon.getSecondaryItemId())
        .discountRate(coupon.getDiscountRate())
        .expirationDate(coupon.getExpirationDate())
        .isActive(coupon.getIsActive())
        .createdAt(coupon.getCreatedAt())
        .isExpired(coupon.isExpired())
        .isDeleted(coupon.isDeleted())
        .build();
  }
}
