package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerSessionPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerSessionPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerSessionPassRepository;
import java.time.LocalDate;
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
  @DisplayName("성공: 고객 선불권 등록")
  void registCustomerSessionPass_success() {
    // given
    CustomerSessionPassRegistRequest request = new CustomerSessionPassRegistRequest();
    request.setCustomerId(1L);
    request.setSessionPassId(2L);
    request.setRemainingCount(10);
    request.setExpirationDate(LocalDate.now().plusDays(30));

    // when
    service.registCustomerSessionPass(request);

    // then
    verify(customerSessionPassRepository, times(1)).save(any(CustomerSessionPass.class));
  }
}
