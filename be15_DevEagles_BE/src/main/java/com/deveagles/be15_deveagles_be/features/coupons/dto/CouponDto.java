package com.deveagles.be15_deveagles_be.features.coupons.dto;

import com.deveagles.be15_deveagles_be.features.coupons.entity.Coupon;
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
public class CouponDto {

  private Long id;
  private String couponCode;
  private String couponTitle;
  private Long staffId;
  private Long primaryItemId;
  private Long secondaryItemId;
  private Integer discountRate;
  private LocalDate expirationDate;
  private Boolean isActive;
  private LocalDateTime createdAt;

  public static CouponDto from(Coupon coupon) {
    return CouponDto.builder()
        .id(coupon.getId())
        .couponCode(coupon.getCouponCode())
        .couponTitle(coupon.getCouponTitle())
        .staffId(coupon.getStaffId())
        .primaryItemId(coupon.getPrimaryItemId())
        .secondaryItemId(coupon.getSecondaryItemId())
        .discountRate(coupon.getDiscountRate())
        .expirationDate(coupon.getExpirationDate())
        .isActive(coupon.getIsActive())
        .createdAt(coupon.getCreatedAt())
        .build();
  }
}
