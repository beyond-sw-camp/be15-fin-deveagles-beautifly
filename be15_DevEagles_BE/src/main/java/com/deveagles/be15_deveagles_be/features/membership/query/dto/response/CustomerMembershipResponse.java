package com.deveagles.be15_deveagles_be.features.membership.query.dto.response;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerMembershipResponse {

  private Long customerId;
  private String customerName;
  private String phoneNumber;
  private Integer totalRemainingPrepaidAmount;
  private List<SessionPassInfo> sessionPasses;
  private LocalDate expirationDate;
}
