package com.deveagles.be15_deveagles_be.features.membership.command.application.service;

import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.PrepaidPassRegistRequest;

public interface PrepaidPassCommandService {
  void registPrepaidPass(PrepaidPassRegistRequest request);
}
