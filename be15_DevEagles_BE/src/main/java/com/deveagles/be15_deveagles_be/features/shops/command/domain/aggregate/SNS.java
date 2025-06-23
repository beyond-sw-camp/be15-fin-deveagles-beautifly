package com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "sns")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SNS {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "sns_id", nullable = false)
  private Long snsId;

  @Column(name = "sns_address", nullable = false)
  private String snsAddress;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private SNSType type;

  @Builder
  public SNS(SNSType type, String snsAddress, Long shopId) {
    this.type = type;
    this.snsAddress = snsAddress;
    this.shopId = shopId;
  }

  public void setAddress(String snsAddress) {
    this.snsAddress = snsAddress;
  }
}
