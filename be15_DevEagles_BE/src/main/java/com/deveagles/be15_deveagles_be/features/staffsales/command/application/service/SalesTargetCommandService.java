package com.deveagles.be15_deveagles_be.features.staffsales.command.application.service;

import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request.SetSalesTargetRequest;

public interface SalesTargetCommandService {
  void setSalesTarget(Long shopId, SetSalesTargetRequest request);
}
