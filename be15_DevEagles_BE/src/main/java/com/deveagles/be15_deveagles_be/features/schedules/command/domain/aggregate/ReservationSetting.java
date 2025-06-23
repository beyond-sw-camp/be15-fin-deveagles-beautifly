package com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalTime;
import lombok.*;

@Entity
@Table(name = "reservation_setting")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationSetting {

  @Id
  @Column(name = "shop_id")
  private Long shopId;

  @Column(name = "available_day", nullable = false)
  private Integer availableDay;

  @Column(name = "available_start_time", nullable = false)
  private LocalTime availableStartTime;

  @Column(name = "available_end_time", nullable = false)
  private LocalTime availableEndTime;

  @Column(name = "lunch_start_time")
  private LocalTime lunchStartTime;

  @Column(name = "lunch_end_time")
  private LocalTime lunchEndTime;
}
