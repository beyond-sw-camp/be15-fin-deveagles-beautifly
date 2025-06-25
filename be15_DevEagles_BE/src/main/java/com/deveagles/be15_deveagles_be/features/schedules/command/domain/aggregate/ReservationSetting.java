package com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.*;

@Entity
@Table(name = "reservation_setting")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationSetting {

  @EmbeddedId private ReservationSettingId id;

  @Column(name = "available_start_time", nullable = false)
  private LocalTime availableStartTime;

  @Column(name = "available_end_time", nullable = false)
  private LocalTime availableEndTime;

  @Column(name = "lunch_start_time")
  private LocalTime lunchStartTime;

  @Column(name = "lunch_end_time")
  private LocalTime lunchEndTime;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  public void markDeleted() {
    this.deletedAt = LocalDateTime.now();
  }

  public boolean isActive() {
    return this.deletedAt == null;
  }

  public void update(LocalTime start, LocalTime end, LocalTime lunchStart, LocalTime lunchEnd) {
    if (start.isAfter(end)) {
      throw new BusinessException(ErrorCode.INVALID_RESERVATION_TIME_RANGE);
    }
    if (lunchStart != null && lunchEnd != null) {
      if (lunchStart.isBefore(start) || lunchEnd.isAfter(end)) {
        throw new BusinessException(ErrorCode.INVALID_LUNCH_TIME_RANGE);
      }
      if (!lunchStart.isBefore(lunchEnd)) {
        throw new BusinessException(ErrorCode.INVALID_LUNCH_TIME_ORDER);
      }
    }

    this.availableStartTime = start;
    this.availableEndTime = end;
    this.lunchStartTime = lunchStart;
    this.lunchEndTime = lunchEnd;
  }

  public Long getShopId() {
    return id.getShopId();
  }

  public Integer getAvailableDay() {
    return id.getAvailableDay();
  }

  public void restore() {
    this.deletedAt = null;
  }
}
