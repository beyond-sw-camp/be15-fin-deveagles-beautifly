package com.deveagles.be15_deveagles_be.features.users.query.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffListInfo {

  private Long staffId;
  private String staffName;
  private String loginId;
  private String phoneNumber;
  private String grade;
  private String colorCode;
  private boolean isWorking;
}
