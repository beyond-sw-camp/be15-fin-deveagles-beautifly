package com.deveagles.be15_deveagles_be.features.sales.command.application.service;

import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.ItemSalesRequest;

public interface ItemSalesCommandService {

  void registItemSales(ItemSalesRequest request);
}
