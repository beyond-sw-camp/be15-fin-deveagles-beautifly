package com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentsInfo {

  @NotNull(message = "결제 수단을 입력해주세요") private PaymentsMethod paymentsMethod;

  @NotNull(message = "결제 금액을 입력해주세요") private Integer amount;

  private Long customerPrepaidPassId;
  private Long customerSessionPassId;
  private Integer usedCount;
}
