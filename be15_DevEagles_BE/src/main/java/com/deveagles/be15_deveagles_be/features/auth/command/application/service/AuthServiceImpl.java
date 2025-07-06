package com.deveagles.be15_deveagles_be.features.auth.command.application.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.common.jwt.JwtTokenProvider;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.CheckEmailRequest;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.EmailVerifyRequest;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.LoginRequest;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.response.TokenResponse;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import jakarta.mail.MessagingException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final RefreshTokenService refreshTokenService;
  private final RedisTemplate<String, String> redisTemplate;
  private final MailService mailService;

  @Value("${spring.mail.properties.auth-code-expiration-millis}")
  private long expireMinute;

  @Override
  public TokenResponse login(LoginRequest request) {

    Staff staff =
        userRepository
            .findStaffByLoginId(request.loginId())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NAME_NOT_FOUND));

    if (!passwordEncoder.matches(request.password(), staff.getPassword())) {
      throw new BusinessException(ErrorCode.USER_INVALID_PASSWORD);
    }

    String accessToken = jwtTokenProvider.createToken(staff.getLoginId());
    String refreshToken = jwtTokenProvider.createRefreshToken(staff.getLoginId());

    refreshTokenService.saveRefreshToken(staff.getLoginId(), refreshToken);

    return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  @Override
  public TokenResponse refreshToken(String refreshToken) {
    // 리프레시 토큰 유효성 검사
    jwtTokenProvider.validateToken(refreshToken);
    String username = jwtTokenProvider.getUsernameFromJWT(refreshToken);

    // Redis에서 저장된 refresh token 가져오기
    String redisKey = "RT:" + username;
    String storedToken = redisTemplate.opsForValue().get(redisKey);

    if (storedToken == null) {
      throw new BadCredentialsException("해당 유저로 저장된 리프레시 토큰 없음");
    }

    if (!storedToken.equals(refreshToken)) {
      throw new BadCredentialsException("리프레시 토큰 일치하지 않음");
    }

    Staff staff =
        userRepository
            .findStaffByLoginId(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NAME_NOT_FOUND));

    // 새로운 토큰 재발급
    String newAccessToken = jwtTokenProvider.createToken(staff.getLoginId());
    String newRefreshToken = jwtTokenProvider.createRefreshToken(staff.getLoginId());

    redisTemplate
        .opsForValue()
        .set(
            redisKey,
            newRefreshToken,
            jwtTokenProvider.getRefreshExpiration(),
            TimeUnit.MILLISECONDS);

    return TokenResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
  }

  @Override
  public void logout(String refreshToken, String accessToken) {

    // 토큰 검증
    jwtTokenProvider.validateToken(refreshToken);
    String username = jwtTokenProvider.getUsernameFromJWT(refreshToken);
    refreshTokenService.deleteRefreshToken(username);

    long remainTime = jwtTokenProvider.getRemainingExpiration(accessToken);
    redisTemplate.opsForValue().set("BL:" + accessToken, "logout", Duration.ofMillis(remainTime));
  }

  @Override
  public void sendPatchPwdEmail(CheckEmailRequest request) {

    userRepository
        .findStaffForGetPwd(request.staffName(), request.email())
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    if (getAuthCode(request.email()) != null) {
      throw new BusinessException(ErrorCode.DUPLICATE_SEND_AUTH_EXCEPTION);
    }

    String authCode = UUID.randomUUID().toString().substring(0, 6);
    saveAuthCode(request.email(), authCode);

    try {
      mailService.sendFindPwdEmail(request.email(), authCode);
    } catch (MessagingException e) {
      throw new BusinessException(ErrorCode.SEND_EMAIL_FAILURE_EXCEPTION);
    }
  }

  @Override
  public void verifyAuthCode(EmailVerifyRequest request) {
    String authCode = getAuthCode(request.email());

    if (authCode == null || !authCode.equals(request.authCode())) {
      throw new BusinessException(ErrorCode.INVALID_AUTH_CODE);
    }

    deleteAuthCode(authCode);
  }

  private void saveAuthCode(String email, String code) {
    redisTemplate.opsForValue().set(email, code, Duration.ofMillis(expireMinute));
  }

  private String getAuthCode(String email) {
    return redisTemplate.opsForValue().get(email);
  }

  private void deleteAuthCode(String code) {
    redisTemplate.delete(code);
  }
}
