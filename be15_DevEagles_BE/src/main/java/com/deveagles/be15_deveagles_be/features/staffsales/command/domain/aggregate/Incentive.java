package com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
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

  @Enumerated(EnumType.STRING)
  @Column(name = "payments_method", nullable = false)
  private PaymentsMethod paymentsMethod;

  @Column(name = "incentive", nullable = false)
  private int incentive;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "staff_id")
  private Long staffId;

  @Column(name = "is_active", nullable = false)
  private boolean isActive = false;

  @Builder
  public Incentive(
      ProductType type,
      PaymentsMethod paymentsMethod,
      int incentive,
      Long shopId,
      Long staffId,
      boolean isActive) {
    this.type = type;
    this.paymentsMethod = paymentsMethod;
    this.incentive = incentive;
    this.shopId = shopId;
    this.staffId = staffId;
    this.isActive = isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  public void setIncentiveRatio(int incentive) {
    this.incentive = incentive;
  }
}
