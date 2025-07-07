package com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "customer_prepaid_pass")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CustomerPrepaidPass {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "customer_prepaid_pass_id")
  private Long customerPrepaidPassId;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "prepaid_pass_id", nullable = false)
  private Long prepaidPassId;

  @Column(name = "remaining_amount")
  private Integer remainingAmount;

  @Column(name = "expiration_date")
  private LocalDate expirationDate;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt = LocalDateTime.now();

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
