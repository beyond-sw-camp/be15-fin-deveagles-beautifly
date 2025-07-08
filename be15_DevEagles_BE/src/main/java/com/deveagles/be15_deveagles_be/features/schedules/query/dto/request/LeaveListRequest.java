package com.deveagles.be15_deveagles_be.features.schedules.query.dto.request;

import java.time.LocalDate;

public record LeaveListRequest(
    LocalDate from, LocalDate to, String leaveType, Long staffId, int page, int size) {
  public int getOffset() {
    return page * size;
  }

  public int getLimit() {
    return size;
  }
}
