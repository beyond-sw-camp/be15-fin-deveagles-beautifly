package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import java.time.LocalDate;

public record CreateLeaveRequest(
    Long staffId, String leaveTitle, LocalDate leaveAt, String leaveMemo) {
  public UpdateLeaveRequest toUpdateRequest() {
    return new UpdateLeaveRequest(leaveTitle, leaveAt, leaveMemo);
  }
}
