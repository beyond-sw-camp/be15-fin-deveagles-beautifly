package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerSessionPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.CustomerSessionPassCommandService;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerSessionPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerSessionPassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerSessionPassCommandServiceImpl implements CustomerSessionPassCommandService {

  private final CustomerSessionPassRepository customerSessionPassRepository;

  @Override
  public void registCustomerSessionPass(CustomerSessionPassRegistRequest request) {

    CustomerSessionPass customerSessionPass =
        CustomerSessionPass.builder()
            .customerId(request.getCustomerId())
            .sessionPassId(request.getSessionPassId())
            .remainingCount(request.getRemainingCount())
            .expirationDate(request.getExpirationDate())
            .build();

    customerSessionPassRepository.save(customerSessionPass);
  }
}
