package com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate;

import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "session_pass")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SessionPass {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "session_pass_id")
  private Long sessionPassId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shop_id", nullable = false)
  private Shop shopId;

  @Column(name = "session_pass_name", length = 100, nullable = false)
  private String sessionPassName;

  @Column(name = "session_pass_price", nullable = false)
  private Integer sessionPassPrice;

  @Column(name = "session", nullable = false)
  private Integer session;

  @Column(name = "expiration_period", nullable = false)
  private Integer expirationPeriod;

  @Column(name = "bonus")
  private Integer bonus;

  @Column(name = "discount_rate")
  private Integer discountRate;

  @Column(name = "session_pass_memo", length = 255)
  private String sessionPassMemo;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt = LocalDateTime.now();

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  public void updateSessionPass(
      String SessionPassName,
      Integer SessionPassPrice,
      Integer Session,
      Integer expirationPeriod,
      Integer bonus,
      Integer discountRate,
      String sessionPassMemo) {
    this.sessionPassName = SessionPassName;
    this.sessionPassPrice = SessionPassPrice;
    this.session = Session;
    this.expirationPeriod = expirationPeriod;
    this.bonus = bonus;
    this.discountRate = discountRate;
    this.sessionPassMemo = sessionPassMemo;
    this.modifiedAt = LocalDateTime.now();
  }
}
