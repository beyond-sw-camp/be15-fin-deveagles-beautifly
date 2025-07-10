package com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffIncentiveInfo {
  private Long staffId;
  private String staffName;
  private Map<PaymentsMethod, ProductIncentiveRates> incentives;
}
