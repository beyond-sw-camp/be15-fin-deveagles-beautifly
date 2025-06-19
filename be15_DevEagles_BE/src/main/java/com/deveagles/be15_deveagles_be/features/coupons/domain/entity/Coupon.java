package com.deveagles.be15_deveagles_be.features.coupons.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Coupon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "coupon_id")
  private Long id;

  @Column(name = "coupon_code", nullable = false, length = 50, unique = true)
  private String couponCode;

  @Column(name = "coupon_title", nullable = false, length = 50)
  private String couponTitle;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "staff_id")
  private Long staffId;

  @Column(name = "primary_item_id", nullable = false)
  private Long primaryItemId;

  @Column(name = "secondary_item_id")
  private Long secondaryItemId;

  @Column(name = "discount_rate", nullable = false)
  private Integer discountRate;

  @Column(name = "expiration_date")
  private LocalDate expirationDate;

  @Column(name = "is_active", nullable = false)
  @Builder.Default
  private Boolean isActive = false;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  // Business methods
  public void activate() {
    this.isActive = true;
  }

  public void deactivate() {
    this.isActive = false;
  }

  public void softDelete() {
    this.deletedAt = LocalDateTime.now();
    this.isActive = false;
  }

  public boolean isDeleted() {
    return this.deletedAt != null;
  }

  public boolean isExpired() {
    return this.expirationDate != null && this.expirationDate.isBefore(LocalDate.now());
  }

  public boolean isUsableForShop(Long requestShopId) {
    return this.shopId.equals(requestShopId);
  }

  public boolean isUsableForStaff(Long requestStaffId) {
    return this.staffId == null || this.staffId.equals(requestStaffId);
  }

  public boolean isUsableForItem(Long requestPrimaryItemId, Long requestSecondaryItemId) {
    if (!this.primaryItemId.equals(requestPrimaryItemId)) {
      return false;
    }
    return this.secondaryItemId == null || this.secondaryItemId.equals(requestSecondaryItemId);
  }
}
