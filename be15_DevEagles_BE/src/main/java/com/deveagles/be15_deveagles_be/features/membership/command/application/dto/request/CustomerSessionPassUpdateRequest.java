package com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerSessionPassUpdateRequest {

  @NotNull(message = "고객 횟수권 ID는 필수입니다.") private Long customerSessionPassId;

  private Integer remainingCount;

  private Date expirationDate;
}
