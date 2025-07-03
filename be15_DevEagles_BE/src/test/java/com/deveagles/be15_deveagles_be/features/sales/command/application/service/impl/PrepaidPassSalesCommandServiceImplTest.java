package com.deveagles.be15_deveagles_be.features.sales.command.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.PaymentsInfo;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.PrepaidPassSalesRequest;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.PaymentsRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.PrepaidPassSalesRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.SalesRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PrepaidPassSalesCommandServiceImplTest {

  private PrepaidPassSalesRepository prepaidPassSalesRepository;
  private SalesRepository salesRepository;
  private PaymentsRepository paymentsRepository;
  private PrepaidPassSalesCommandServiceImpl service;

  @BeforeEach
  void setUp() {
    prepaidPassSalesRepository = mock(PrepaidPassSalesRepository.class);
    salesRepository = mock(SalesRepository.class);
    paymentsRepository = mock(PaymentsRepository.class);
    service =
        new PrepaidPassSalesCommandServiceImpl(
            prepaidPassSalesRepository, salesRepository, paymentsRepository);
  }

  @Test
  @DisplayName("실패: 선불권 ID 없음")
  void throwsWhenPrepaidPassIdIsNull() {
    PrepaidPassSalesRequest request = validRequest();
    request.setPrepaidPassId(null);

    assertBusinessException(request, ErrorCode.PREPAIDPASS_NOT_FOUND);
  }

  @Test
  @DisplayName("실패: 고객 ID 없음")
  void throwsWhenCustomerIdIsNull() {
    PrepaidPassSalesRequest request = validRequest();
    request.setCustomerId(null);

    assertBusinessException(request, ErrorCode.CUSTOMER_NOT_FOUND);
  }

  @Test
  @DisplayName("실패: 매장 ID 없음")
  void throwsWhenShopIdIsNull() {
    PrepaidPassSalesRequest request = validRequest();
    request.setShopId(null);

    assertBusinessException(request, ErrorCode.SHOP_NOT_FOUND);
  }

  @Test
  @DisplayName("실패: staff ID 없음")
  void throwsWhenStaffIdIsNull() {
    PrepaidPassSalesRequest request = validRequest();
    request.setStaffId(null);

    assertBusinessException(request, ErrorCode.USER_NOT_FOUND);
  }

  @Test
  @DisplayName("실패: 정가가 null")
  void throwsWhenRetailPriceIsNull() {
    PrepaidPassSalesRequest request = validRequest();
    request.setRetailPrice(null);

    assertBusinessException(request, ErrorCode.SALES_RETAILPRICE_REQUIRED);
  }

  @Test
  @DisplayName("실패: 정가가 음수")
  void throwsWhenRetailPriceNegative() {
    PrepaidPassSalesRequest request = validRequest();
    request.setRetailPrice(-1000);

    assertBusinessException(request, ErrorCode.SALES_RETAILPRICE_REQUIRED);
  }

  @Test
  @DisplayName("실패: 결제금액이 null")
  void throwsWhenTotalAmountIsNull() {
    PrepaidPassSalesRequest request = validRequest();
    request.setTotalAmount(null);

    assertBusinessException(request, ErrorCode.SALES_TOTALAMOUNT_REQUIRED);
  }

  @Test
  @DisplayName("실패: 결제금액이 음수")
  void throwsWhenTotalAmountNegative() {
    PrepaidPassSalesRequest request = validRequest();
    request.setTotalAmount(-1);

    assertBusinessException(request, ErrorCode.SALES_TOTALAMOUNT_REQUIRED);
  }

  @Test
  @DisplayName("실패: salesDate가 없음")
  void throwsWhenSalesDateNull() {
    PrepaidPassSalesRequest request = validRequest();
    request.setSalesDate(null);

    assertBusinessException(request, ErrorCode.SALES_SALESDATE_REQUIRED);
  }

  @Test
  @DisplayName("실패: 결제 수단이 null")
  void throwsWhenPaymentMethodNull() {
    PrepaidPassSalesRequest request = validRequest();
    request.setPayments(List.of(new PaymentsInfo(null, 1000)));

    assertBusinessException(request, ErrorCode.SALES_PAYMENTMETHOD_REQUIRED);
  }

  @Test
  @DisplayName("실패: 결제 금액이 null")
  void throwsWhenPaymentAmountNull() {
    PrepaidPassSalesRequest request = validRequest();
    request.setPayments(List.of(new PaymentsInfo(PaymentsMethod.CARD, null)));

    assertBusinessException(request, ErrorCode.SALES_PAYMENTSAMOUNT_REQUIRED);
  }

  @Test
  @DisplayName("실패: 결제 금액이 0 이하")
  void throwsWhenPaymentAmountZero() {
    PrepaidPassSalesRequest request = validRequest();
    request.setPayments(List.of(new PaymentsInfo(PaymentsMethod.CARD, 0)));

    assertBusinessException(request, ErrorCode.SALES_PAYMENTSAMOUNT_REQUIRED);
  }

  private PrepaidPassSalesRequest validRequest() {

    PrepaidPassSalesRequest request = new PrepaidPassSalesRequest();
    request.setPrepaidPassId(1L);
    request.setCustomerId(2L);
    request.setStaffId(3L);
    request.setShopId(4L);
    request.setRetailPrice(10000);
    request.setTotalAmount(9000);
    request.setSalesDate(LocalDateTime.now());
    request.setPayments(List.of(new PaymentsInfo(PaymentsMethod.CARD, 9000)));
    return request;
  }

  private void assertBusinessException(PrepaidPassSalesRequest request, ErrorCode expectedCode) {
    BusinessException ex =
        assertThrows(BusinessException.class, () -> service.registPrepaidPassSales(request));
    assertEquals(expectedCode, ex.getErrorCode());
  }
}
