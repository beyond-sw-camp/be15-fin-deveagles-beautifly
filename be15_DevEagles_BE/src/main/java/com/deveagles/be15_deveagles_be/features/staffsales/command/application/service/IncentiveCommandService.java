package com.deveagles.be15_deveagles_be.features.staffsales.command.application.service;

import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.IncentiveListResult;

public interface IncentiveCommandService {
  IncentiveListResult getIncentives(Long shopId);
}
