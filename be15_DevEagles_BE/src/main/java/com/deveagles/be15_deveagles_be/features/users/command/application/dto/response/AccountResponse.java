package com.deveagles.be15_deveagles_be.features.users.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountResponse {

  private final String email;
  private final String phoneNumber;
}
