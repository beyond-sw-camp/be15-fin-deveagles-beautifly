package com.deveagles.be15_deveagles_be.features.sales.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.PaymentsInfo;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.PrepaidPassSalesRequest;
import com.deveagles.be15_deveagles_be.features.sales.command.application.service.PrepaidPassSalesCommandService;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.Payments;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PrepaidPassSales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.Sales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.PaymentsRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.PrepaidPassSalesRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.SalesRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrepaidPassSalesCommandServiceImpl implements PrepaidPassSalesCommandService {

  private final PrepaidPassSalesRepository prepaidPassSalesRepository;
  private final SalesRepository salesRepository;
  private final PaymentsRepository paymentsRepository;

  @Transactional
  @Override
  public void registPrepaidPassSales(PrepaidPassSalesRequest request) {

    if (request.getPrepaidPassId() == null)
      throw new BusinessException(ErrorCode.PREPAIDPASS_NOT_FOUND);
    if (request.getCustomerId() == null) throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND);
    if (request.getShopId() == null) throw new BusinessException(ErrorCode.SHOP_NOT_FOUND);
    if (request.getRetailPrice() == null || request.getRetailPrice() < 0)
      throw new BusinessException(ErrorCode.SALES_RETAILPRICE_REQUIRED);
    if (request.getTotalAmount() == null || request.getTotalAmount() < 0)
      throw new BusinessException(ErrorCode.SALES_TOTALAMOUNT_REQUIRED);
    if (request.getStaffId() == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);

    if (request.getSalesDate() == null)
      throw new BusinessException(ErrorCode.SALES_SALESDATE_REQUIRED);
    for (PaymentsInfo payment : request.getPayments()) {
      if (payment.getPaymentsMethod() == null) {
        throw new BusinessException(ErrorCode.SALES_PAYMENTMETHOD_REQUIRED);
      }
      if (payment.getAmount() == null || payment.getAmount() <= 0) {
        throw new BusinessException(ErrorCode.SALES_PAYMENTSAMOUNT_REQUIRED);
      }
    }

    // 1. Sales 저장
    Sales sales =
        Sales.builder()
            .shopId(request.getShopId())
            .customerId(request.getCustomerId())
            .staffId(request.getStaffId())
            .reservationId(request.getReservationId())
            .retailPrice(request.getRetailPrice())
            .discountRate(request.getDiscountRate())
            .discountAmount(request.getDiscountAmount())
            .totalAmount(request.getTotalAmount())
            .salesMemo(request.getSalesMemo())
            .salesDate(request.getSalesDate())
            .isRefunded(false)
            .build();
    salesRepository.save(sales);

    // 2. PrepaidPassSales 저장
    PrepaidPassSales prepaidPassSales =
        PrepaidPassSales.builder()
            .salesId(sales.getSalesId())
            .prepaidPassId(request.getPrepaidPassId())
            .build();
    prepaidPassSalesRepository.save(prepaidPassSales);

    // 3. Payment 저장
    List<Payments> payments =
        request.getPayments().stream()
            .map(
                p ->
                    Payments.builder()
                        .salesId(sales.getSalesId())
                        .paymentsMethod(p.getPaymentsMethod())
                        .amount(p.getAmount())
                        .build())
            .toList();

    for (Payments payment : payments) {
      paymentsRepository.save(payment);
    }
  }
}
