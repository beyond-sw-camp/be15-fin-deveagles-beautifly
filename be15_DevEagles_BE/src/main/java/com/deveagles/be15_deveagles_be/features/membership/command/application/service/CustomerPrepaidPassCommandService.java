package com.deveagles.be15_deveagles_be.features.membership.command.application.service;

import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerPrepaidPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerPrepaidPassUpdateRequest;

public interface CustomerPrepaidPassCommandService {
  void registCustomerPrepaidPass(CustomerPrepaidPassRegistRequest request);

  void updateCustomerPrepaidPass(CustomerPrepaidPassUpdateRequest request);
}
