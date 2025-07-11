package com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerPrepaidPassRegistRequest {

  @NotNull(message = "고객ID는 필수입니다.") private Long customerId;

  @NotNull(message = "선불권ID는 필수입니다.") private Long prepaidPassId;

  @NotNull(message = "충전금액은 필수입니다.") private Integer remainingAmount;

  @NotNull(message = "유효기간은 필수입니다.") private Date expirationDate;
}
