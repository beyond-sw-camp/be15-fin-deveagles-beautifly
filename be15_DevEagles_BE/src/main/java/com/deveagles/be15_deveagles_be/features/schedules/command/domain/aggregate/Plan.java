package com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "plan")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Plan {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "plan_id")
  private Long planId;

  @Column(name = "staff_id", nullable = false)
  private Long staffId;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "plan_title", nullable = false, length = 50)
  private String planTitle;

  @Column(name = "plan_memo")
  private String planMemo;

  @Column(name = "plan_start_at", nullable = false)
  private LocalDateTime planStartAt;

  @Column(name = "plan_end_at", nullable = false)
  private LocalDateTime planEndAt;
}
