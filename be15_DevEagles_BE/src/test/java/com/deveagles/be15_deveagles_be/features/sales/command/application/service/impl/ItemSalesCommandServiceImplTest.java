package com.deveagles.be15_deveagles_be.features.sales.command.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.CustomerRepository;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerPrepaidPassRepository;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerSessionPassRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.ItemSalesRequest;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.PaymentsInfo;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.ItemSalesRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.PaymentsRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.SalesRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ItemSalesCommandServiceImplTest {

  private CustomerPrepaidPassRepository prepaidRepository;
  private CustomerSessionPassRepository sessionRepository;
  private SalesRepository salesRepository;
  private PaymentsRepository paymentsRepository;
  private CustomerRepository customerRepository;
  private ItemSalesRepository itemSalesRepository;
  private ItemSalesCommandServiceImpl service;

  @BeforeEach
  void setUp() {
    prepaidRepository = mock(CustomerPrepaidPassRepository.class);
    sessionRepository = mock(CustomerSessionPassRepository.class);
    salesRepository = mock(SalesRepository.class);
    paymentsRepository = mock(PaymentsRepository.class);
    customerRepository = mock(CustomerRepository.class);
    itemSalesRepository = mock(ItemSalesRepository.class);

    service =
        new ItemSalesCommandServiceImpl(
            prepaidRepository,
            sessionRepository,
            salesRepository,
            paymentsRepository,
            customerRepository,
            itemSalesRepository);
  }

  @Test
  @DisplayName("실패: 정가 음수")
  void failWhenRetailPriceNegative() {
    ItemSalesRequest req = validRequest();
    req.setRetailPrice(-1);
    assertThrowsWithCode(() -> service.registItemSales(req), ErrorCode.SALES_RETAILPRICE_REQUIRED);
  }

  @Test
  @DisplayName("실패: 총 결제 금액 음수")
  void failWhenTotalAmountNegative() {
    ItemSalesRequest req = validRequest();
    req.setTotalAmount(-1);
    assertThrowsWithCode(() -> service.registItemSales(req), ErrorCode.SALES_TOTALAMOUNT_REQUIRED);
  }

  @Test
  @DisplayName("실패: 결제 수단 null")
  void failWhenPaymentMethodNull() {
    ItemSalesRequest req = validRequest();
    req.setPayments(List.of(new PaymentsInfo(null, 1000, null, null, null)));
    assertThrowsWithCode(
        () -> service.registItemSales(req), ErrorCode.SALES_PAYMENTMETHOD_REQUIRED);
  }

  @Test
  @DisplayName("성공: 등록")
  void successRegistItemSales() {
    ItemSalesRequest req = validRequest();

    Customer customer = mock(Customer.class);
    when(customerRepository.findById(req.getCustomerId())).thenReturn(Optional.of(customer));

    service.registItemSales(req);

    verify(salesRepository).save(any());
    verify(paymentsRepository).save(any());
    verify(itemSalesRepository).save(any());
    verify(customer).incrementVisitCount();
    verify(customer).addRevenue(req.getTotalAmount());
  }

  private ItemSalesRequest validRequest() {
    ItemSalesRequest req = new ItemSalesRequest();
    req.setShopId(1L);
    req.setCustomerId(2L);
    req.setStaffId(3L);
    req.setReservationId(4L);
    req.setSecondaryItemId(5L);
    req.setQuantity(1);
    req.setRetailPrice(10000);
    req.setDiscountRate(10);
    req.setDiscountAmount(1000);
    req.setTotalAmount(9000);
    req.setSalesMemo("테스트 메모");
    req.setSalesDate(LocalDateTime.now());
    req.setPayments(List.of(new PaymentsInfo(PaymentsMethod.CARD, 9000, null, null, null)));
    return req;
  }

  private void assertThrowsWithCode(Runnable executable, ErrorCode code) {
    BusinessException e = assertThrows(BusinessException.class, executable::run);
    assertEquals(code, e.getErrorCode());
  }
}
