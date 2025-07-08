package com.deveagles.be15_deveagles_be.features.membership.query.dto.response;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerPrepaidPassDetailInfo {

  private Long customerPrepaidPassId;
  private String MembershipType;
  private String prepaidPassName;
  private Integer remainingAmount;
  private Date expirationDate;
  private Date createdAt;
}
