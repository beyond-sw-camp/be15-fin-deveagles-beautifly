package com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
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

  @Setter
  @Column(name = "remaining_amount")
  private Integer remainingAmount;

  @Setter
  @Column(name = "expiration_date")
  private Date expirationDate;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Setter
  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt = LocalDateTime.now();

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  public void useAmount(int amount) {
    this.remainingAmount -= amount;
    this.modifiedAt = LocalDateTime.now();
  }

  public void restoreAmount(int amount) {
    this.remainingAmount += amount;
    this.modifiedAt = LocalDateTime.now();
  }
}
