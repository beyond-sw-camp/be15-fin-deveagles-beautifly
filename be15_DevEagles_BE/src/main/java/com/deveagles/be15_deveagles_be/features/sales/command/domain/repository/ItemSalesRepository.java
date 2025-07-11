package com.deveagles.be15_deveagles_be.features.sales.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.ItemSales;

public interface ItemSalesRepository {
  ItemSales save(ItemSales build);
}
