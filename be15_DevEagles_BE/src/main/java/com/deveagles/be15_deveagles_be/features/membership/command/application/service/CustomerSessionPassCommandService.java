package com.deveagles.be15_deveagles_be.features.membership.command.application.service;

import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerSessionPassRegistRequest;

public interface CustomerSessionPassCommandService {
  void registCustomerSessionPass(CustomerSessionPassRegistRequest request);
}
