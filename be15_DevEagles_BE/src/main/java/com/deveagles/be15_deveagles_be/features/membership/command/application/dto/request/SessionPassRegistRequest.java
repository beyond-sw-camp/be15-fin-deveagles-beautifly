package com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SessionPassRegistRequest {

  @NotNull(message = "shopId는 필수입니다.") private Long shopId;

  @NotBlank(message = "횟수권명은 필수입니다.")
  private String sessionPassName;

  @NotNull(message = "횟수권 가격은 필수입니다.") private Integer sessionPassPrice;

  @NotNull(message = "횟수는 필수입니다.") private Integer session;

  @NotNull(message = "유효기간 입력은 필수입니다.") private Integer expirationPeriod;

  private Integer bonus;

  private Integer discountRate;

  private String SessionPassMemo;
}
