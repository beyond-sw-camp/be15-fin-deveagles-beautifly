package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerPrepaidPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerPrepaidPassUpdateRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerPrepaidPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerPrepaidPassRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerPrepaidPassCommandServiceImplTest {

  private CustomerPrepaidPassRepository customerPrepaidPassRepository;
  private CustomerPrepaidPassCommandServiceImpl service;

  @BeforeEach
  void setUp() {
    customerPrepaidPassRepository = mock(CustomerPrepaidPassRepository.class);
    service = new CustomerPrepaidPassCommandServiceImpl(customerPrepaidPassRepository);
  }

  @Test
  @DisplayName("성공: 고객 선불권 등록")
  void registCustomerPrepaidPass_success() {
    // given
    CustomerPrepaidPassRegistRequest request = new CustomerPrepaidPassRegistRequest();
    request.setCustomerId(1L);
    request.setPrepaidPassId(2L);
    request.setRemainingAmount(10000);

    // Date +30일
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, 30);
    request.setExpirationDate(calendar.getTime());

    // when
    service.registCustomerPrepaidPass(request);

    // then
    verify(customerPrepaidPassRepository, times(1)).save(any(CustomerPrepaidPass.class));
  }

  @Test
  @DisplayName("성공: 고객 선불권 수정")
  void updateCustomerPrepaidPass_success() {
    // given
    Long passId = 1L;
    CustomerPrepaidPassUpdateRequest request = new CustomerPrepaidPassUpdateRequest();
    request.setCustomerPrepaidPassId(passId);
    request.setRemainingAmount(50000);
    request.setExpirationDate(new Date());

    CustomerPrepaidPass pass = mock(CustomerPrepaidPass.class);
    when(customerPrepaidPassRepository.findById(passId)).thenReturn(Optional.of(pass));

    // when
    service.updateCustomerPrepaidPass(request);

    // then
    verify(pass, times(1)).setRemainingAmount(50000);
    verify(pass, times(1)).setExpirationDate(any(Date.class));
    verify(pass, times(1)).setModifiedAt(any());
    verify(customerPrepaidPassRepository, times(1)).save(pass);
  }

  @Test
  @DisplayName("실패: 고객 선불권이 존재하지 않음")
  void updateCustomerPrepaidPass_notFound() {
    // given
    Long passId = 999L;
    CustomerPrepaidPassUpdateRequest request = new CustomerPrepaidPassUpdateRequest();
    request.setCustomerPrepaidPassId(passId);

    when(customerPrepaidPassRepository.findById(passId)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex =
        assertThrows(BusinessException.class, () -> service.updateCustomerPrepaidPass(request));

    assertEquals(ErrorCode.CUSTOMERPREPAIDPASS_NOT_FOUND, ex.getErrorCode());
  }
}
