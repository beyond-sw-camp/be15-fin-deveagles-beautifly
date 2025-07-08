package com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Reservation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reservation_id")
  private Long reservationId;

  @Column(name = "staff_id", nullable = false)
  private Long staffId;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "customer_id")
  private Long customerId;

  @Enumerated(EnumType.STRING)
  @Column(name = "reservation_status_name")
  private ReservationStatusName reservationStatusName;

  @Column(name = "staff_memo")
  private String staffMemo;

  @Column(name = "reservation_memo")
  private String reservationMemo;

  @Column(name = "reservation_start_at", nullable = false)
  private LocalDateTime reservationStartAt;

  @Column(name = "reservation_end_at", nullable = false)
  private LocalDateTime reservationEndAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt;

  @Setter
  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    this.modifiedAt = LocalDateTime.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.modifiedAt = LocalDateTime.now();
  }

  public void update(
      Long staffId,
      ReservationStatusName reservationStatusName,
      String staffMemo,
      String reservationMemo,
      LocalDateTime reservationStartAt,
      LocalDateTime reservationEndAt) {
    this.staffId = staffId;
    this.reservationStatusName = reservationStatusName;
    this.staffMemo = staffMemo;
    this.reservationMemo = reservationMemo;
    this.reservationStartAt = reservationStartAt;
    this.reservationEndAt = reservationEndAt;
  }

  public void changeStatus(ReservationStatusName newStatus) {
    if (this.reservationStatusName == ReservationStatusName.PAID) {
      throw new BusinessException(ErrorCode.MODIFY_NOT_ALLOWED_FOR_PAID_RESERVATION);
    }
    this.reservationStatusName = newStatus;
  }
}
