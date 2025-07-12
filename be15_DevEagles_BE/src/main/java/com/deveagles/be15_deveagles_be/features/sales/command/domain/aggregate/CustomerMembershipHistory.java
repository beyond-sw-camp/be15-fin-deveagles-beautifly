package com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "customer_membership_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CustomerMembershipHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "customer_membership_history_id")
  private Long customerMembershipHistoryId;

  @Column(name = "sales_id", nullable = false)
  private Long salesId;

  @Column(name = "payments_id", nullable = false)
  private Long paymentsId;

  @Column(name = "customer_prepaid_pass_id")
  private Long customerPrepaidPassId;

  @Column(name = "customer_session_pass_id")
  private Long customerSessionPassId;

  @Column(name = "used_count")
  private Integer usedCount;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  public void softDelete() {
    this.deletedAt = LocalDateTime.now();
  }
}
