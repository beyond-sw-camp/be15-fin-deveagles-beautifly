package com.deveagles.be15_deveagles_be.features.membership.query.dto.response;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.ExpirationPeriodType;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.PrepaidPass;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrepaidPassResponse {

  private Long prepaidPassId;
  private Long shopId;
  private String prepaidPassName;
  private Integer prepaidPassPrice;
  private Integer expirationPeriod;
  private ExpirationPeriodType expirationPeriodType;
  private Integer bonus;
  private Integer discountRate;
  private String prepaidPassMemo;
  private String type;

  public static PrepaidPassResponse from(PrepaidPass pass) {
    return PrepaidPassResponse.builder()
        .prepaidPassId(pass.getPrepaidPassId())
        .shopId(pass.getShopId().getShopId())
        .prepaidPassName(pass.getPrepaidPassName())
        .prepaidPassPrice(pass.getPrepaidPassPrice())
        .expirationPeriod(pass.getExpirationPeriod())
        .expirationPeriodType(pass.getExpirationPeriodType())
        .bonus(pass.getBonus())
        .discountRate(pass.getDiscountRate())
        .prepaidPassMemo(pass.getPrepaidPassMemo())
        .type("PREPAID")
        .build();
  }
}
