package com.deveagles.be15_deveagles_be.features.membership.query.dto.request;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerExpiringPrepaidPassFilterRequest {
  private Integer minRemainingAmount; // 최소 잔여 선불권
  private Integer maxRemainingAmount; // 최대 잔여 선불권
  private Date startDate;
  private Date endDate;

  private Integer page = 1;
  private Integer size = 10;

  public int getOffset() {
    return (page - 1) * size;
  }
}
