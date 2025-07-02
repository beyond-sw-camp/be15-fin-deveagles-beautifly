package com.deveagles.be15_deveagles_be.features.auth.command.application.service;

import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.LoginRequest;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.response.TokenResponse;
import jakarta.validation.Valid;

public interface AuthService {
  TokenResponse login(@Valid LoginRequest request);

  TokenResponse refreshToken(String refreshToken);

  void logout(String refreshToken, String accessToken);
}
