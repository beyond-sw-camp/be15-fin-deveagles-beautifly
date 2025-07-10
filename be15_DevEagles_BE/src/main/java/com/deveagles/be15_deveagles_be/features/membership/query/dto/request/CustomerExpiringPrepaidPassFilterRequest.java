package com.deveagles.be15_deveagles_be.features.membership.query.dto.request;

import java.util.Date;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@Data
public class CustomerExpiringPrepaidPassFilterRequest {
  private Integer minRemainingAmount; // 최소 잔여 선불권
  private Integer maxRemainingAmount; // 최대 잔여 선불권
  private String customerKeyword;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date startDate;

  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private Date endDate;

  private Integer page = 1;
  private Integer size = 10;

  public int getOffset() {
    return (page - 1) * size;
  }
}
