package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import java.time.LocalDate;

public record UpdateLeaveRequest(String leaveTitle, LocalDate leaveAt, String leaveMemo) {}
