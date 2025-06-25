package com.deveagles.be15_deveagles_be.features.items.command.application.service;

import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.PrimaryItemRegistRequest;

public interface PrimaryItemServiceCommand {
  void registerPrimaryItem(PrimaryItemRegistRequest request);
}
