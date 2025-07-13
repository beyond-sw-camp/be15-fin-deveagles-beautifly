package com.deveagles.be15_deveagles_be.features.staffsales.query.service;

import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request.GetSalesTargetRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.SalesTargetListResult;

public interface SalesTargetQueryService {
  SalesTargetListResult getSalesTarget(Long shopId, GetSalesTargetRequest request);
}
