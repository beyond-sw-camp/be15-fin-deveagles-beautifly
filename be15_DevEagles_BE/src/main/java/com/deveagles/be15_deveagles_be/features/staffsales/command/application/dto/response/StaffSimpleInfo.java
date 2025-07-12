package com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffSimpleInfo {

  private Long staffId;
  private String staffName;
}
