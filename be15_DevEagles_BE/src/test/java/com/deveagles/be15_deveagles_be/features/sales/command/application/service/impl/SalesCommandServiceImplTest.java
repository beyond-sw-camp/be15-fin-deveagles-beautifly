package com.deveagles.be15_deveagles_be.features.sales.command.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.Sales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.SalesRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SalesCommandServiceImplTest {

  @Mock private SalesRepository salesRepository;

  @InjectMocks private SalesCommandServiceImpl salesCommandService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("성공: 매출 환불 처리")
  void refundSales_success() {
    // given
    Long salesId = 1L;
    Sales sales = mock(Sales.class);
    when(sales.isRefunded()).thenReturn(false);
    when(salesRepository.findById(salesId)).thenReturn(Optional.of(sales));

    // when
    salesCommandService.refundSales(salesId);

    // then
    verify(sales).setRefunded(true);
  }

  @Test
  @DisplayName("예외: 존재하지 않는 매출 ID")
  void refundSales_salesNotFound() {
    // given
    Long salesId = 1L;
    when(salesRepository.findById(salesId)).thenReturn(Optional.empty());

    // when & then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> salesCommandService.refundSales(salesId));
    assertEquals(ErrorCode.SALES_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("예외: 이미 환불된 매출")
  void refundSales_alreadyRefunded() {
    // given
    Long salesId = 1L;
    Sales sales = mock(Sales.class);
    when(sales.isRefunded()).thenReturn(true);
    when(salesRepository.findById(salesId)).thenReturn(Optional.of(sales));

    // when & then
    BusinessException exception =
        assertThrows(BusinessException.class, () -> salesCommandService.refundSales(salesId));
    assertEquals(ErrorCode.SALES_ALREADY_REFUNDED, exception.getErrorCode());
  }
}
