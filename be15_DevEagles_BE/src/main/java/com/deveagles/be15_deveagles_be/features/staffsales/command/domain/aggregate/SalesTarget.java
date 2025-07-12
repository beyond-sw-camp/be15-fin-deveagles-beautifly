package com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate;

import jakarta.persistence.*;
import java.sql.Date;
import lombok.*;

@Getter
@Entity
@Table(name = "sales_target")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SalesTarget {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "target_id", nullable = false)
  private Long targetId;

  @Column(name = "staff_id")
  private Long staffId;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "membership", nullable = false)
  private boolean membership = false;

  @Column(name = "items", nullable = false)
  private boolean items = false;

  @Column(name = "sales", nullable = false)
  private int sales;

  @Column(name = "target_year", nullable = false)
  private Date targetYear;

  @Column(name = "target_month", nullable = false)
  private int targetMonth;

  @Column(name = "apply_status", nullable = false)
  private boolean applyStatus = false;

  @Builder
  public SalesTarget(
      Long staffId,
      Long shopId,
      boolean membership,
      boolean items,
      int sales,
      Date targetYear,
      int targetMonth,
      boolean applyStatus) {
    this.staffId = staffId;
    this.shopId = shopId;
    this.membership = membership;
    this.items = items;
    this.sales = sales;
    this.targetYear = targetYear;
    this.targetMonth = targetMonth;
    this.applyStatus = applyStatus;
  }

  public void setStatus(boolean status) {
    this.applyStatus = status;
  }
}
