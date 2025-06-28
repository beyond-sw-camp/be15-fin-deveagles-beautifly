package com.deveagles.be15_deveagles_be.features.auth.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.common.jwt.JwtTokenProvider;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.LoginRequest;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.response.TokenResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.service.AuthServiceImpl;
import com.deveagles.be15_deveagles_be.features.auth.command.application.service.RefreshTokenService;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
public class AuthServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtTokenProvider jwtTokenProvider;

  @Mock private RefreshTokenService refreshTokenService;

  private AuthServiceImpl authService;

  @BeforeEach
  void setUp() {
    authService =
        new AuthServiceImpl(userRepository, passwordEncoder, jwtTokenProvider, refreshTokenService);
  }

  @Test
  @DisplayName("로그인 성공 시 accessToken과 refreshToken을 반환한다")
  void 로그인_성공() {
    // given
    LoginRequest request = new LoginRequest("user01", "password123");

    Staff staff = Staff.builder().loginId("user01").password("encodedPw").build();

    when(userRepository.findStaffByLoginId("user01")).thenReturn(Optional.of(staff));
    when(passwordEncoder.matches("password123", "encodedPw")).thenReturn(true);
    when(jwtTokenProvider.createToken("user01")).thenReturn("access-token");
    when(jwtTokenProvider.createRefreshToken("user01")).thenReturn("refresh-token");

    // when
    TokenResponse response = authService.login(request);

    // then
    assertEquals("access-token", response.getAccessToken());
    assertEquals("refresh-token", response.getRefreshToken());
    verify(refreshTokenService).saveRefreshToken("user01", "refresh-token");
  }

  @Test
  @DisplayName("존재하지 않는 로그인 ID는 예외를 발생시킨다")
  void 로그인_아이디_없음_예외() {
    // given
    LoginRequest request = new LoginRequest("nonexistent", "password123");
    when(userRepository.findStaffByLoginId("nonexistent")).thenReturn(Optional.empty());

    // when & then
    BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));
    assertEquals(ErrorCode.USER_NAME_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  @DisplayName("비밀번호가 일치하지 않으면 예외를 발생시킨다")
  void 로그인_비밀번호_불일치_예외() {
    // given
    LoginRequest request = new LoginRequest("user01", "wrongPassword");

    Staff staff = Staff.builder().loginId("user01").password("encodedPw").build();

    when(userRepository.findStaffByLoginId("user01")).thenReturn(Optional.of(staff));
    when(passwordEncoder.matches("wrongPassword", "encodedPw")).thenReturn(false);

    // when & then
    BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));
    assertEquals(ErrorCode.USER_INVALID_PASSWORD, ex.getErrorCode());
  }
}
