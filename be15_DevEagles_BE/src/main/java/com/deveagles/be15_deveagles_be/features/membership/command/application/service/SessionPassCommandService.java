package com.deveagles.be15_deveagles_be.features.membership.command.application.service;

import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.SessionPassRegistRequest;

public interface SessionPassCommandService {
  void registSessionPass(SessionPassRegistRequest request);
}
