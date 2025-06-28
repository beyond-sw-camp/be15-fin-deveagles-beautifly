package com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "`leave`")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Leave {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "leave_id")
  private Long leaveId;

  @Column(name = "staff_id", nullable = false)
  private Long staffId;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "leave_title", nullable = false, length = 50)
  private String leaveTitle;

  @Column(name = "leave_at", nullable = false)
  private LocalDate leaveAt;

  @Column(name = "leave_memo")
  private String leaveMemo;

  public void update(String title, LocalDate date, String memo) {
    this.leaveTitle = title;
    this.leaveAt = date;
    this.leaveMemo = memo;
  }
}
