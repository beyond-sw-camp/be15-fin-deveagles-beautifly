package com.deveagles.be15_deveagles_be.features.schedules.query.dto.request;

import java.time.LocalDate;

public record BookedTimeRequest(Long staffId, LocalDate from) {}
