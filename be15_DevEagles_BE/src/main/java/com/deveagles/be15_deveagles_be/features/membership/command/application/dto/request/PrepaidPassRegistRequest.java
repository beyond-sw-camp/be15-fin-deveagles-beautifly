package com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PrepaidPassRegistRequest {

  @NotNull(message = "shopId는 필수입니다.") private Long shopId;

  @NotBlank(message = "선불권명은 필수입니다.")
  private String prepaidPassName;

  @NotNull(message = "선불권 가격은 필수입니다.") private Integer prepaidPassPrice;

  @NotNull(message = "선불권 유효기간은 필수입니다.") private Integer expirationPeriod;

  private Integer bonus;

  private Integer discountRate;

  private String prepaidPassMemo;
}
