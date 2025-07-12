package com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response;

import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.StaffIncentiveInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.IncentiveType;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IncentiveListResult {

  private Long shopId;
  private Boolean incentiveEnabled;
  private IncentiveType incentiveType;
  List<StaffSimpleInfo> staffList;
  List<StaffIncentiveInfo> incentiveList;
}
