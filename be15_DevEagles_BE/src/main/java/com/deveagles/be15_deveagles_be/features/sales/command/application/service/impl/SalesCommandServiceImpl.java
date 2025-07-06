package com.deveagles.be15_deveagles_be.features.sales.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.sales.command.application.service.SalesCommandService;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.Sales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.SalesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesCommandServiceImpl implements SalesCommandService {

  private final SalesRepository salesRepository;

  @Transactional
  @Override
  public void refundSales(Long salesId) {
    Sales sales =
        salesRepository
            .findById(salesId)
            .orElseThrow(() -> new BusinessException(ErrorCode.SALES_NOT_FOUND));

    if (sales.isRefunded()) {
      throw new BusinessException(ErrorCode.SALES_ALREADY_REFUNDED);
    }

    sales.setRefunded(true);
  }
}
