package com.deveagles.be15_deveagles_be.features.membership.query.dto.response;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrepaidPassInfo {
  private String prepaidPassName;
  private Integer remainingAmount;
  private Date expirationDate;
}
