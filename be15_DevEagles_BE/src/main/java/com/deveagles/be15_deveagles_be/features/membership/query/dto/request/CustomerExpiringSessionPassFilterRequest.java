package com.deveagles.be15_deveagles_be.features.membership.query.dto.request;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerExpiringSessionPassFilterRequest {
  private Integer minRemainingCount; // 최소 잔여 횟수권
  private Integer maxRemainingCount; // 최대 잔여 횟수권

  private Date startDate;
  private Date endDate;

  private Integer page = 1;
  private Integer size = 10;

  public int getOffset() {
    return (page - 1) * size;
  }
}
