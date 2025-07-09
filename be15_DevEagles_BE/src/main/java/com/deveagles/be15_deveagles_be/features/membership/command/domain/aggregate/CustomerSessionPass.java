package com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.*;

@Entity
@Table(name = "customer_session_pass")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CustomerSessionPass {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "customer_session_pass_id")
  private Long customerSessionPassId;

  @Column(name = "customer_id", nullable = false)
  private Long customerId;

  @Column(name = "session_pass_id", nullable = false)
  private Long sessionPassId;

  @Setter
  @Column(name = "remaining_count")
  private Integer remainingCount;

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

  public void useCount(int count) {
    this.remainingCount -= count;
  }
}
