package com.deveagles.be15_deveagles_be.features.sales.command.application.service;

import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.PrepaidPassSalesRequest;

public interface PrepaidPassSalesCommandService {
  void registPrepaidPassSales(PrepaidPassSalesRequest request);
}
