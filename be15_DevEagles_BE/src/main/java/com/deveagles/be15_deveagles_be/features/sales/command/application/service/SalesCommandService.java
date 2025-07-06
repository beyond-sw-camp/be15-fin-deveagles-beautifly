package com.deveagles.be15_deveagles_be.features.sales.command.application.service;

import jakarta.transaction.Transactional;

public interface SalesCommandService {

  void refundSales(Long salesId);

  @Transactional
  void deleteSales(Long salesId);
}
