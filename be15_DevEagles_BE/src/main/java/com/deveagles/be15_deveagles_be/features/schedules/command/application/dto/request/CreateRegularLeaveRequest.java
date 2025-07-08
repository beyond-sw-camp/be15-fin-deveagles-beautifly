package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.DayOfWeekEnum;

public record CreateRegularLeaveRequest(
    Long staffId,
    String regularLeaveTitle,
    Integer monthlyLeave,
    DayOfWeekEnum weeklyLeave,
    String regularLeaveMemo) {
  public UpdateRegularLeaveRequest toUpdateRequest() {
    return new UpdateRegularLeaveRequest(
        regularLeaveTitle, monthlyLeave, weeklyLeave, regularLeaveMemo);
  }
}
