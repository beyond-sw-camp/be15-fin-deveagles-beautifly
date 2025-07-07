package com.deveagles.be15_deveagles_be.features.users.command.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StaffPermissions {

  private Long accessId;
  private String accessName;
  private boolean canRead;
  private boolean canWrite;
  private boolean canDelete;
  private boolean isActive;
}
