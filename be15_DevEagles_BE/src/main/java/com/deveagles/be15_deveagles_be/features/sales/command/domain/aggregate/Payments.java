package com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payments {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "payments_id")
  private Long paymentsId;

  @Column(name = "sales_id", nullable = false)
  private Long salesId;

  @Enumerated(EnumType.STRING)
  @Column(name = "payments_method")
  private PaymentsMethod paymentsMethod;

  @Column(name = "amount")
  private Integer amount;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  public void softDelete() {
    this.deletedAt = LocalDateTime.now();
  }
}
