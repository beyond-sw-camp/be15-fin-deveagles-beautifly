package com.deveagles.be15_deveagles_be.features.auth.command.application.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.common.jwt.JwtTokenProvider;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.LoginRequest;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.response.TokenResponse;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
