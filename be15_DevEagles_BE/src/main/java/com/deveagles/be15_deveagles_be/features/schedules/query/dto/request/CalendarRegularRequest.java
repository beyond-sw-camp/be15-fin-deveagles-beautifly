package com.deveagles.be15_deveagles_be.features.schedules.query.dto.request;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ScheduleType;
import java.time.LocalDate;

public record CalendarRegularRequest(ScheduleType scheduleType, LocalDate from, LocalDate to) {}
