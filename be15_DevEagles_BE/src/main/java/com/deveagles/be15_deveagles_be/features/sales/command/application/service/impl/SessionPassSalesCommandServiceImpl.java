package com.deveagles.be15_deveagles_be.features.sales.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerSessionPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.CustomerSessionPassCommandService;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.ExpirationPeriodType;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.SessionPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.SessionPassRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.PaymentsInfo;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.SessionPassSalesRequest;
import com.deveagles.be15_deveagles_be.features.sales.command.application.service.SessionPassSalesCommandService;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.Payments;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.Sales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SessionPassSales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.PaymentsRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.SalesRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.SessionPassSalesRepository;
import jakarta.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionPassSalesCommandServiceImpl implements SessionPassSalesCommandService {

  private final SessionPassSalesRepository sessionPassSalesRepository;
  private final SessionPassRepository sessionPassRepository;
  private final SalesRepository salesRepository;
  private final PaymentsRepository paymentsRepository;
  private final CustomerSessionPassCommandService customerSessionPassCommandService;

  @Transactional
  @Override
  public void registSessionPassSales(SessionPassSalesRequest request) {
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

    // 2. SessionPassSales 저장
    SessionPassSales sessionPassSales =
        SessionPassSales.builder()
            .salesId(sales.getSalesId())
            .sessionPassId(request.getSessionPassId())
            .build();
    sessionPassSalesRepository.save(sessionPassSales);

    // 3. Payments 저장
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

    // 4. 고객 횟수권 등록
    SessionPass sessionPass =
        sessionPassRepository
            .findById(request.getSessionPassId())
            .orElseThrow(() -> new BusinessException(ErrorCode.SESSIONPASS_NOT_FOUND));

    Date expirationDate =
        calculateExpirationDate(
            sessionPass.getExpirationPeriod(), sessionPass.getExpirationPeriodType());

    int baseCount = sessionPass.getSession();
    int bonusCount = sessionPass.getBonus() != null ? sessionPass.getBonus() : 0;

    CustomerSessionPassRegistRequest passRequest = new CustomerSessionPassRegistRequest();
    passRequest.setCustomerId(request.getCustomerId());
    passRequest.setSessionPassId(sessionPass.getSessionPassId());
    passRequest.setRemainingCount(baseCount + bonusCount);
    passRequest.setExpirationDate(expirationDate);

    customerSessionPassCommandService.registCustomerSessionPass(passRequest);
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
  public void updateSessionPassSales(Long salesId, SessionPassSalesRequest request) {
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

    // 1. 기존 Sales 조회 및 업데이트
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

    // 2. 기존 SessionPassSales 삭제 후 저장
    sessionPassSalesRepository.deleteBySalesId(salesId);
    SessionPassSales sessionPassSales =
        SessionPassSales.builder()
            .salesId(salesId)
            .sessionPassId(request.getSessionPassId())
            .build();
    sessionPassSalesRepository.save(sessionPassSales);

    // 3. 기존 Payments 삭제 후 재등록
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
