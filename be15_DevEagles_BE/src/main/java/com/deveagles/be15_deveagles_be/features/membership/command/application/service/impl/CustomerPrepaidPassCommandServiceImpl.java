package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerPrepaidPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.CustomerPrepaidPassCommandService;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerPrepaidPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerPrepaidPassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerPrepaidPassCommandServiceImpl implements CustomerPrepaidPassCommandService {

  private final CustomerPrepaidPassRepository customerPrepaidPassRepository;

  @Override
  public void registCustomerPrepaidPass(CustomerPrepaidPassRegistRequest request) {

    CustomerPrepaidPass customerPrepaidPass =
        CustomerPrepaidPass.builder()
            .customerId(request.getCustomerId())
            .prepaidPassId(request.getPrepaidPassId())
            .remainingAmount(request.getRemainingAmount())
            .expirationDate(request.getExpirationDate())
            .build();

    customerPrepaidPassRepository.save(customerPrepaidPass);
  }
}
