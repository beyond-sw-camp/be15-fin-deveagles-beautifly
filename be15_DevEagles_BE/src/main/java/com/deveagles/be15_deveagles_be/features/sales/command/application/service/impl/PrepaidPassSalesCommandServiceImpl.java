package com.deveagles.be15_deveagles_be.features.sales.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerPrepaidPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.CustomerPrepaidPassCommandService;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.ExpirationPeriodType;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.PrepaidPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.PrepaidPassRepository;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrepaidPassSalesCommandServiceImpl implements PrepaidPassSalesCommandService {

  private final PrepaidPassSalesRepository prepaidPassSalesRepository;
  private final PrepaidPassRepository prepaidPassRepository;
  private final SalesRepository salesRepository;
  private final PaymentsRepository paymentsRepository;
  private final CustomerPrepaidPassCommandService customerPrepaidPassCommandService;

  @Transactional
  @Override
  public void registPrepaidPassSales(PrepaidPassSalesRequest request) {

    // 유효성 검사
    if (request.getRetailPrice() < 0) {
      throw new BusinessException(ErrorCode.SALES_RETAILPRICE_REQUIRED);
    }
    if (request.getTotalAmount() < 0) {
      throw new BusinessException(ErrorCode.SALES_TOTALAMOUNT_REQUIRED);
    }
    for (PaymentsInfo payment : request.getPayments()) {
      if (payment.getAmount() == null || payment.getAmount() <= 0) {
        throw new BusinessException(ErrorCode.SALES_PAYMENTSAMOUNT_REQUIRED);
      }
      if (payment.getPaymentsMethod() == null) {
        throw new BusinessException(ErrorCode.SALES_PAYMENTMETHOD_REQUIRED);
      }
    }

    // Sales 저장
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

    // PrepaidPassSales 저장
    PrepaidPassSales prepaidPassSales =
        PrepaidPassSales.builder()
            .salesId(sales.getSalesId())
            .prepaidPassId(request.getPrepaidPassId())
            .build();
    prepaidPassSalesRepository.save(prepaidPassSales);

    // Payments 저장
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
    payments.forEach(paymentsRepository::save);

    // 고객 선불권 등록
    PrepaidPass prepaidPass =
        prepaidPassRepository
            .findById(request.getPrepaidPassId())
            .orElseThrow(() -> new BusinessException(ErrorCode.PREPAIDPASS_NOT_FOUND));

    Date expirationDate =
        calculateExpirationDate(
            prepaidPass.getExpirationPeriod(), prepaidPass.getExpirationPeriodType());

    int baseAmount = request.getRetailPrice();
    int bonusAmount = prepaidPass.getBonus() != null ? prepaidPass.getBonus() : 0;

    CustomerPrepaidPassRegistRequest passRequest = new CustomerPrepaidPassRegistRequest();
    passRequest.setCustomerId(request.getCustomerId());
    passRequest.setPrepaidPassId(prepaidPass.getPrepaidPassId());
    passRequest.setRemainingAmount(baseAmount + bonusAmount);
    passRequest.setExpirationDate(expirationDate);

    customerPrepaidPassCommandService.registCustomerPrepaidPass(passRequest);
  }

  private Date calculateExpirationDate(int period, ExpirationPeriodType type) {
    Calendar calendar = Calendar.getInstance();
    switch (type) {
      case DAY -> calendar.add(Calendar.DAY_OF_YEAR, period);
      case WEEK -> calendar.add(Calendar.WEEK_OF_YEAR, period);
      case MONTH -> calendar.add(Calendar.MONTH, period);
      case YEAR -> calendar.add(Calendar.YEAR, period);
    }
    return calendar.getTime();
  }

  @Transactional
  @Override
  public void updatePrepaidPassSales(Long salesId, PrepaidPassSalesRequest request) {
    if (request.getRetailPrice() < 0) {
      throw new BusinessException(ErrorCode.SALES_RETAILPRICE_REQUIRED);
    }
    if (request.getTotalAmount() < 0) {
      throw new BusinessException(ErrorCode.SALES_TOTALAMOUNT_REQUIRED);
    }
    for (PaymentsInfo payment : request.getPayments()) {
      if (payment.getAmount() == null || payment.getAmount() <= 0) {
        throw new BusinessException(ErrorCode.SALES_PAYMENTSAMOUNT_REQUIRED);
      }
      if (payment.getPaymentsMethod() == null) {
        throw new BusinessException(ErrorCode.SALES_PAYMENTMETHOD_REQUIRED);
      }
    }

    Sales sales =
        salesRepository
            .findById(salesId)
            .orElseThrow(() -> new BusinessException(ErrorCode.SALES_NOT_FOUND));

    sales.update(
        request.getShopId(),
        request.getCustomerId(),
        request.getStaffId(),
        request.getReservationId(),
        request.getRetailPrice(),
        request.getDiscountRate(),
        request.getDiscountAmount(),
        request.getTotalAmount(),
        request.getSalesMemo(),
        request.getSalesDate());

    prepaidPassSalesRepository.deleteBySalesId(salesId);
    PrepaidPassSales prepaidPassSales =
        PrepaidPassSales.builder()
            .salesId(salesId)
            .prepaidPassId(request.getPrepaidPassId())
            .build();
    prepaidPassSalesRepository.save(prepaidPassSales);

    paymentsRepository.deleteBySalesId(salesId);
    List<Payments> payments =
        request.getPayments().stream()
            .map(
                p ->
                    Payments.builder()
                        .salesId(salesId)
                        .paymentsMethod(p.getPaymentsMethod())
                        .amount(p.getAmount())
                        .build())
            .toList();
    payments.forEach(paymentsRepository::save);
  }
}
