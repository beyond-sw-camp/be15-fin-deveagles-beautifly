package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerSessionPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerSessionPassUpdateRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerSessionPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerSessionPassRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerSessionPassCommandServiceImplTest {

  private CustomerSessionPassRepository customerSessionPassRepository;
  private CustomerSessionPassCommandServiceImpl service;

  @BeforeEach
  void setUp() {
    customerSessionPassRepository = mock(CustomerSessionPassRepository.class);
    service = new CustomerSessionPassCommandServiceImpl(customerSessionPassRepository);
  }

  @Test
  @DisplayName("성공: 고객 횟수권 등록")
  void registCustomerSessionPass_success() {
    // given
    CustomerSessionPassRegistRequest request = new CustomerSessionPassRegistRequest();
    request.setCustomerId(1L);
    request.setSessionPassId(2L);
    request.setRemainingCount(10);

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.DATE, 30);
    request.setExpirationDate(calendar.getTime());

    // when
    service.registCustomerSessionPass(request);

    // then
    verify(customerSessionPassRepository, times(1)).save(any(CustomerSessionPass.class));
  }

  @Test
  @DisplayName("성공: 고객 횟수권 수정")
  void updateCustomerSessionPass_success() {
    // given
    Long passId = 1L;
    CustomerSessionPassUpdateRequest request = new CustomerSessionPassUpdateRequest();
    request.setCustomerSessionPassId(passId);
    request.setRemainingCount(5);
    request.setExpirationDate(new Date());

    CustomerSessionPass pass = mock(CustomerSessionPass.class);
    when(customerSessionPassRepository.findById(passId)).thenReturn(Optional.of(pass));

    // when
    service.updateCustomerSessionPass(request);

    // then
    verify(pass, times(1)).setRemainingCount(5);
    verify(pass, times(1)).setExpirationDate(any(Date.class));
    verify(pass, times(1)).setModifiedAt(any());
    verify(customerSessionPassRepository, times(1)).save(pass);
  }

  @Test
  @DisplayName("실패: 고객 횟수권이 존재하지 않음")
  void updateCustomerSessionPass_notFound() {
    // given
    Long passId = 999L;
    CustomerSessionPassUpdateRequest request = new CustomerSessionPassUpdateRequest();
    request.setCustomerSessionPassId(passId);

    when(customerSessionPassRepository.findById(passId)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex =
        assertThrows(BusinessException.class, () -> service.updateCustomerSessionPass(request));

    assertEquals(ErrorCode.CUSTOMERSESSIONPASS_NOT_FOUND, ex.getErrorCode());
  }
}
