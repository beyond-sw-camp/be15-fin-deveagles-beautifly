package com.deveagles.be15_deveagles_be.features.sales.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.Payments;
import java.util.List;

public interface PaymentsRepository {

  Payments save(Payments payments);

  void deleteBySalesId(Long salesId);

  List<Payments> findAllBySalesId(Long salesId);
}
