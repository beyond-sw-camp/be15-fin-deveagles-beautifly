package com.deveagles.be15_deveagles_be.features.auth.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.LoginRequest;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.response.TokenResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Auth 관련 API")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "로그인", description = "사용자가 사이트에 로그인합니다.")
  @PostMapping("/login")
  public ResponseEntity<ApiResponse<TokenResponse>> login(
      @RequestBody @Valid LoginRequest request) {

    TokenResponse response = authService.login(request);

    return buildTokenResponse(response);
  }

  private ResponseEntity<ApiResponse<TokenResponse>> buildTokenResponse(TokenResponse response) {

    ResponseCookie cookie = createRefreshTokenCookie(response.getRefreshToken());

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(ApiResponse.success(response));
  }

  private ResponseCookie createRefreshTokenCookie(String refreshToken) {

    return ResponseCookie.from("refreshToken", refreshToken)
        .httpOnly(true)
        .path("/")
        .maxAge(Duration.ofDays(7))
        .sameSite("Strict")
        .build();
  }
}
