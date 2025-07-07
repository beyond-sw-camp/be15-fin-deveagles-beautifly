package com.deveagles.be15_deveagles_be.features.users.command.application.dto.response;

import com.deveagles.be15_deveagles_be.features.auth.command.domain.aggregate.AccessAuth;
import java.sql.Date;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffInfoResponse {
  private String staffName;
  private String email;
  private String phoneNumber;
  private String grade;
  private String staffStatus;
  private boolean isWorking;
  private Date joinedDate;
  private Date leftDate;
  private String description;
  private List<AccessAuth> accessList;
  private String colorCode;
}
