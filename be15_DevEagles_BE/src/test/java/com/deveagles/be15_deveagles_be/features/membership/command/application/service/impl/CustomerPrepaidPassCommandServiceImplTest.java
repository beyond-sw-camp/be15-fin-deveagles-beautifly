package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerPrepaidPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerPrepaidPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerPrepaidPassRepository;
import java.time.LocalDate;
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
    request.setExpirationDate(LocalDate.now().plusDays(30));

    // when
    service.registCustomerPrepaidPass(request);

    // then
    verify(customerPrepaidPassRepository, times(1)).save(any(CustomerPrepaidPass.class));
  }
}
