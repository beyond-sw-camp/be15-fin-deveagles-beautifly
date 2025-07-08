package com.deveagles.be15_deveagles_be.features.membership.query.dto.response;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerSessionPassDetailInfo {

  private Long customerSessionPassId;
  private String MembershipType;
  private String sessionPassName;
  private Integer remainingCount;
  private Date expirationDate;
  private Date createdAt;
}
