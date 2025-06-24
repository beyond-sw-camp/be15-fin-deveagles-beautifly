package com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "incentive")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Incentive {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "incentive_id", nullable = false)
  private Long incentiveId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private ProductType type;

  @Column(name = "incentive", nullable = false)
  private int incentive;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "staff_id")
  private Long staffId;

  @Column(name = "is_active", nullable = false)
  private boolean isActive = false;

  @Builder
  public Incentive(ProductType type, int incentive, Long shopId, Long staffId, boolean isActive) {
    this.type = type;
    this.incentive = incentive;
    this.shopId = shopId;
    this.staffId = staffId;
    this.isActive = isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }
}
