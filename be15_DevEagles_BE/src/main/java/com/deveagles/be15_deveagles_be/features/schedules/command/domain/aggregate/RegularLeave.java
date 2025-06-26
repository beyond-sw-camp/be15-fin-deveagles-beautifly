package com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "regular_leave")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RegularLeave {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "regular_leave_id")
  private Long regularLeaveId;

  @Column(name = "staff_id", nullable = false)
  private Long staffId;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "regular_leave_title", nullable = false, length = 50)
  private String regularLeaveTitle;

  @Column(name = "monthly_leave")
  private Integer monthlyLeave;

  @Enumerated(EnumType.STRING)
  @Column(name = "weekly_leave")
  private DayOfWeekEnum weeklyLeave;

  @Column(name = "regular_leave_memo")
  private String regularLeaveMemo;

  public void update(String title, Integer monthly, DayOfWeekEnum weekly, String memo) {
    this.regularLeaveTitle = title;
    this.monthlyLeave = monthly;
    this.weeklyLeave = weekly;
    this.regularLeaveMemo = memo;
  }
}
