package com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalTime;
import lombok.*;

@Entity
@Table(name = "regular_plan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RegularPlan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "regular_plan_id")
  private Long regularPlanId;

  @Column(name = "staff_id", nullable = false)
  private Long staffId;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "regular_plan_title", nullable = false, length = 50)
  private String regularPlanTitle;

  @Column(name = "monthly_plan")
  private Integer monthlyPlan;

  @Enumerated(EnumType.STRING)
  @Column(name = "weekly_plan")
  private DayOfWeekEnum weeklyPlan;

  @Column(name = "regular_plan_memo")
  private String regularPlanMemo;

  @Column(name = "regular_plan_start_at", nullable = false)
  private LocalTime regularPlanStartAt;

  @Column(name = "regular_plan_end_at", nullable = false)
  private LocalTime regularPlanEndAt;
}
