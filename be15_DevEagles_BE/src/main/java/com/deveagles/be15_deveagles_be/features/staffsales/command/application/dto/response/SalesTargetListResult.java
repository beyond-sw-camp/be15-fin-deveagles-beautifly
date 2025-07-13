package com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response;

import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.StaffSalesTargetInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.StaffSalesSettingType;
import java.time.Month;
import java.time.Year;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SalesTargetListResult {

  private Year targetYear;
  private Month targetMonth;
  private List<StaffSimpleInfo> staffList;
  private StaffSalesSettingType type;
  private List<StaffSalesTargetInfo> salesTargetInfos;
}
