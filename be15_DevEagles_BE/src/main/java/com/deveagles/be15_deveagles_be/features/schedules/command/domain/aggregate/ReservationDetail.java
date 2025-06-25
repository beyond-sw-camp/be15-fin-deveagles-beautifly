package com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservation_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReservationDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "reservation_detail_id")
  private Long reservationDetailId;

  @Column(name = "reservation_id", nullable = false)
  private Long reservationId;

  @Column(name = "secondary_item_id", nullable = false)
  private Long secondaryItemId;
}
