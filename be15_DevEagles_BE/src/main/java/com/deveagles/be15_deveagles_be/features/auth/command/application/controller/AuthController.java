package com.deveagles.be15_deveagles_be.features.auth.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.CheckEmailRequest;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.EmailVerifyRequest;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
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

  @Operation(summary = "로그아웃", description = "사용자가 사이트에서 로그아웃합니다.")
  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout(
      @CookieValue(name = "refreshToken", required = false) String refreshToken,
      @RequestHeader(name = "Authorization", required = false) String authHeader) { // 토큰 삭제, 만료
    if (refreshToken != null && authHeader != null && authHeader.startsWith("Bearer ")) {
      String accessToken = authHeader.substring(7);
      authService.logout(refreshToken, accessToken);
    }

    ResponseCookie deleteCookie = createDeleteRefreshTokenCookie();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
        .body(ApiResponse.success(null));
  }

  @Operation(summary = "RefreshToken 재발급", description = "accessToken 만료 시 자동으로 Token을 재발급합니다.")
  @PostMapping("/refresh")
  public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
      @CookieValue(name = "refreshToken", required = false) String refreshToken) {
    if (refreshToken == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    TokenResponse tokenResponse = authService.refreshToken(refreshToken);
    return buildTokenResponse(tokenResponse);
  }

  @Operation(summary = "비밀번호 변경 이메일 인증", description = "사용자가 비밀번호 변경을 위한 이메일을 인증합니다.")
  @PostMapping("/check-email")
  public ResponseEntity<ApiResponse<Void>> findPwd(
      @RequestBody @Valid CheckEmailRequest checkEmailRequest) {

    authService.sendPatchPwdEmail(checkEmailRequest);

    return ResponseEntity.ok().body(ApiResponse.success(null));
  }

  @Operation(summary = "비밀번호 변경을 위한 인증 코드 확인", description = "사용자가 비밀번호 변경을 위한 인증 코드를 확인합니다.")
  @PostMapping("/verify")
  public ResponseEntity<ApiResponse<Void>> verifyAuthCode(
      @RequestBody @Valid EmailVerifyRequest verifyRequest) {

    authService.verifyAuthCode(verifyRequest);
    return ResponseEntity.ok().body(ApiResponse.success(null));
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

  private ResponseCookie createDeleteRefreshTokenCookie() {

    return ResponseCookie.from("refreshToken", "")
        .httpOnly(true)
        .path("/")
        .maxAge(0)
        .sameSite("Strict")
        .build();
  }
}
