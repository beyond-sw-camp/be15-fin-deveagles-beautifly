package com.deveagles.be15_deveagles_be.features.membership.query.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerExpiringPrepaidPassResponse {
  private Long customerId;
  private String customerName;
  private String phoneNumber;
  private List<PrepaidPassInfo> prepaidPasses;
}
