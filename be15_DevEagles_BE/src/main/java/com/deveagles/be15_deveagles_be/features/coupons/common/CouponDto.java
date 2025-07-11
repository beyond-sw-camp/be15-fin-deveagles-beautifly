package com.deveagles.be15_deveagles_be.features.coupons.common;

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
public class CouponDto {

  private Long id;
  private String couponCode;
  private String couponTitle;
  private Long shopId;
  private Integer discountRate;
  private LocalDate expirationDate;
  private Boolean isActive;
  private LocalDateTime createdAt;

  private DesignerInfo designerInfo;
  private PrimaryItemInfo primaryItemInfo;
  private SecondaryItemInfo secondaryItemInfo;

  // Constructor for QueryDSL Projections
  public CouponDto(
      Long id,
      String couponCode,
      String couponTitle,
      Long shopId,
      Integer discountRate,
      LocalDate expirationDate,
      Boolean isActive,
      LocalDateTime createdAt,
      Long staffId,
      String staffName,
      Long primaryItemId,
      String primaryItemName,
      String primaryItemCategory,
      Long secondaryItemId,
      String secondaryItemName) {
    this.id = id;
    this.couponCode = couponCode;
    this.couponTitle = couponTitle;
    this.shopId = shopId;
    this.discountRate = discountRate;
    this.expirationDate = expirationDate;
    this.isActive = isActive;
    this.createdAt = createdAt;

    this.designerInfo = new DesignerInfo(staffId, staffName);
    this.primaryItemInfo =
        (primaryItemId != null)
            ? new PrimaryItemInfo(primaryItemId, primaryItemName, primaryItemCategory)
            : null;
    this.secondaryItemInfo = new SecondaryItemInfo(secondaryItemId, secondaryItemName);
  }

  public static CouponDto from(Coupon coupon) {
    return CouponDto.builder()
        .id(coupon.getId())
        .couponCode(coupon.getCouponCode())
        .couponTitle(coupon.getCouponTitle())
        .shopId(coupon.getShopId())
        .discountRate(coupon.getDiscountRate())
        .expirationDate(coupon.getExpirationDate())
        .isActive(coupon.getIsActive())
        .createdAt(coupon.getCreatedAt())
        .designerInfo(new DesignerInfo(coupon.getStaffId(), null))
        .primaryItemInfo(
            coupon.getPrimaryItemId() != null
                ? new PrimaryItemInfo(coupon.getPrimaryItemId(), null, null)
                : null)
        .secondaryItemInfo(new SecondaryItemInfo(coupon.getSecondaryItemId(), null))
        .build();
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DesignerInfo {
    private Long staffId;
    private String staffName;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PrimaryItemInfo {
    private Long itemId;
    private String name;
    private String category;
  }

  @Getter
  @NoArgsConstructor
  public static class SecondaryItemInfo {
    private Long itemId;
    private String name;

    public SecondaryItemInfo(Long itemId, String name) {
      this.itemId = itemId;
      this.name = name;
    }
  }
}
