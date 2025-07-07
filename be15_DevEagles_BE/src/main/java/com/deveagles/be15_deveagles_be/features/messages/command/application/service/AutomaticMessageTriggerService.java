package com.deveagles.be15_deveagles_be.features.messages.command.application.service;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.AutomaticCreateRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.AutomaticEventType;

public interface AutomaticMessageTriggerService {
  void registerAutomaticMessage(Long shopId, AutomaticCreateRequest request);

  void triggerAutomaticSend(Customer customer, AutomaticEventType eventType);
}
