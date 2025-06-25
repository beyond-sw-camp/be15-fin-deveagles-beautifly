package com.deveagles.be15_deveagles_be.features.items.command.application.service;

import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.PrimaryItemRequest;

public interface PrimaryItemCommandService {
  void registerPrimaryItem(PrimaryItemRequest request);
}
