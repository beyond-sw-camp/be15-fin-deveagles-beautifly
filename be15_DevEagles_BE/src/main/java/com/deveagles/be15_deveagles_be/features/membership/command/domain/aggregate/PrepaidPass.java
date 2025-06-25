package com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate;

import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "prepaid_pass")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PrepaidPass {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "prepaid_pass_id")
  private Long prepaidPassId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shop_id", nullable = false)
  private Shop shopId;

  @Column(name = "prepaid_pass_name", length = 100, nullable = false)
  private String prepaidPassName;

  @Column(name = "price", nullable = false)
  private Integer price;

  @Column(name = "expiration_period", nullable = false)
  private Integer expirationPeriod;

  @Column(name = "bonus")
  private Integer bonus;

  @Column(name = "discount_rate")
  private Integer discountRate;

  @Column(name = "prepaid_pass_memo", length = 255)
  private String prepaidPassMemo;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt = LocalDateTime.now();

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
