package com.deveagles.be15_deveagles_be.features.membership.query.dto.response;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.ExpirationPeriodType;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.SessionPass;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SessionPassResponse {

  private Long sessionPassId;
  private String sessionPassName;
  private Long secondaryItemId;
  private Integer sessionPassPrice;
  private Integer session;
  private Integer expirationPeriod;
  private ExpirationPeriodType expirationPeriodType;
  private Integer bonus;
  private Integer discountRate;
  private String sessionPassMemo;
  private String type;

  public static SessionPassResponse from(SessionPass pass) {
    return SessionPassResponse.builder()
        .sessionPassId(pass.getSessionPassId())
        .sessionPassName(pass.getSessionPassName())
        .secondaryItemId(pass.getSecondaryItemId())
        .sessionPassPrice(pass.getSessionPassPrice())
        .session(pass.getSession())
        .expirationPeriod(pass.getExpirationPeriod())
        .expirationPeriodType(pass.getExpirationPeriodType())
        .bonus(pass.getBonus())
        .discountRate(pass.getDiscountRate())
        .sessionPassMemo(pass.getSessionPassMemo())
        .type("SESSION")
        .build();
  }
}
