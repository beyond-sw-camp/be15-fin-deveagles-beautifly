package com.deveagles.be15_deveagles_be.features.auth.command.application.service;

public interface RefreshTokenService {
  void saveRefreshToken(String loginId, String refreshToken);

  void deleteRefreshToken(String username);
}
