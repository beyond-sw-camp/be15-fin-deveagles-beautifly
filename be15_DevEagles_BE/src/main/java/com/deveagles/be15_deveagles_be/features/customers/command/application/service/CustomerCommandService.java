package com.deveagles.be15_deveagles_be.features.customers.command.application.service;

import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateCustomerRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateCustomerRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.CustomerResponse;

public interface CustomerCommandService {

  CustomerResponse createCustomer(CreateCustomerRequest request);

  CustomerResponse updateCustomer(UpdateCustomerRequest request);

  void deleteCustomer(Long customerId, Long shopId);

  CustomerResponse updateMarketingConsent(Long customerId, Long shopId, Boolean consent);

  CustomerResponse updateNotificationConsent(Long customerId, Long shopId, Boolean consent);

  CustomerResponse addVisit(Long customerId, Long shopId, Integer revenue);

  CustomerResponse addNoshow(Long customerId, Long shopId);
}
