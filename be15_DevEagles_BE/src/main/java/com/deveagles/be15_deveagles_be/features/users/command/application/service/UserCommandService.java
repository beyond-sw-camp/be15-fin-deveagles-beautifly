package com.deveagles.be15_deveagles_be.features.users.command.application.service;

import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.UserCreateRequest;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.ValidCheckRequest;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import jakarta.validation.Valid;

public interface UserCommandService {
  Staff userRegist(@Valid UserCreateRequest userRequest, Long shopId);

  Boolean validCheckId(@Valid ValidCheckRequest validRequest);

  Boolean validCheckEmail(@Valid ValidCheckRequest validRequest);
}
