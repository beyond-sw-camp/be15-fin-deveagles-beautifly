package com.deveagles.be15_deveagles_be.features.auth.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.common.jwt.JwtTokenProvider;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.CheckEmailRequest;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.EmailVerifyRequest;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.LoginRequest;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.response.TokenResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.service.AuthServiceImpl;
import com.deveagles.be15_deveagles_be.features.auth.command.application.service.MailService;
import com.deveagles.be15_deveagles_be.features.auth.command.application.service.RefreshTokenService;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import jakarta.mail.MessagingException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
public class AuthServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtTokenProvider jwtTokenProvider;

  @Mock private RefreshTokenService refreshTokenService;

  @Mock private RedisTemplate<String, String> redisTemplate;

  @Mock private ValueOperations<String, String> valueOperations;

  @Mock private MailService mailService;

  private AuthServiceImpl authService;

  @BeforeEach
  void setUp() {
    authService =
        new AuthServiceImpl(
            userRepository,
            passwordEncoder,
            jwtTokenProvider,
            refreshTokenService,
            redisTemplate,
            mailService);
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

  @Test
  @DisplayName("refreshToken: 토큰 재발급 성공")
  void refreshToken_성공() {

    // given
    String oldRefreshToken = "oldRefreshToken";
    String username = "user01";
    String redisKey = "RT:" + username;
    String newAccessToken = "newAccessToken";
    String newRefreshToken = "newRefreshToken";

    Staff staff = Staff.builder().loginId(username).build();

    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(jwtTokenProvider.validateToken(oldRefreshToken)).thenReturn(true);
    Mockito.when(jwtTokenProvider.getUsernameFromJWT(oldRefreshToken)).thenReturn(username);
    Mockito.when(valueOperations.get(redisKey)).thenReturn(oldRefreshToken);
    Mockito.when(userRepository.findStaffByLoginId(username)).thenReturn(Optional.of(staff));
    Mockito.when(jwtTokenProvider.createToken(username)).thenReturn(newAccessToken);
    Mockito.when(jwtTokenProvider.createRefreshToken(username)).thenReturn(newRefreshToken);
    Mockito.when(jwtTokenProvider.getRefreshExpiration()).thenReturn(600000L); // 10분
    // when
    TokenResponse response = authService.refreshToken(oldRefreshToken);

    // then
    assertEquals(newAccessToken, response.getAccessToken());
    assertEquals(newRefreshToken, response.getRefreshToken());
    Mockito.verify(valueOperations).set(redisKey, newRefreshToken, 600000L, TimeUnit.MILLISECONDS);
  }

  @Test
  @DisplayName("refreshToken: Redis에 저장된 토큰이 null이면 예외 발생")
  void refreshToken_저장토큰없음_예외() {
    String token = "fakeToken";
    String username = "userX";
    String redisKey = "RT:" + username;
    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(jwtTokenProvider.validateToken(token)).thenReturn(true);
    Mockito.when(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn(username);
    Mockito.when(valueOperations.get(redisKey)).thenReturn(null);

    assertThrows(BadCredentialsException.class, () -> authService.refreshToken(token));
  }

  @Test
  @DisplayName("refreshToken: 저장된 토큰과 다르면 예외 발생")
  void refreshToken_불일치_예외() {
    String token = "tokenA";
    String username = "userX";
    String redisKey = "RT:" + username;
    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(jwtTokenProvider.validateToken(token)).thenReturn(true);
    Mockito.when(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn(username);
    Mockito.when(valueOperations.get(redisKey)).thenReturn("tokenB"); // 불일치

    assertThrows(BadCredentialsException.class, () -> authService.refreshToken(token));
  }

  @Test
  @DisplayName("refreshToken: 유저 정보가 없으면 예외 발생")
  void refreshToken_유저없음_예외() {
    String token = "validToken";
    String username = "ghost";
    String redisKey = "RT:" + username;
    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(jwtTokenProvider.validateToken(token)).thenReturn(true);
    Mockito.when(jwtTokenProvider.getUsernameFromJWT(token)).thenReturn(username);
    Mockito.when(valueOperations.get(redisKey)).thenReturn(token);
    Mockito.when(userRepository.findStaffByLoginId(username)).thenReturn(Optional.empty());

    BusinessException ex =
        assertThrows(BusinessException.class, () -> authService.refreshToken(token));
    assertEquals(ErrorCode.USER_NAME_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  @DisplayName("logout: 리프레시 삭제 및 액세스 토큰 블랙리스트 처리")
  void logout_성공() {
    // given
    String refreshToken = "refreshToken123";
    String accessToken = "accessToken123";
    String username = "user01";
    long remainMillis = 300000L; // 5분
    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(jwtTokenProvider.validateToken(refreshToken)).thenReturn(true);
    Mockito.when(jwtTokenProvider.getUsernameFromJWT(refreshToken)).thenReturn(username);
    Mockito.when(jwtTokenProvider.getRemainingExpiration(accessToken)).thenReturn(remainMillis);

    // when
    authService.logout(refreshToken, accessToken);

    // then
    Mockito.verify(jwtTokenProvider).validateToken(refreshToken);
    Mockito.verify(jwtTokenProvider).getUsernameFromJWT(refreshToken);
    Mockito.verify(refreshTokenService).deleteRefreshToken(username);
    Mockito.verify(jwtTokenProvider).getRemainingExpiration(accessToken);
    Mockito.verify(valueOperations)
        .set("BL:" + accessToken, "logout", Duration.ofMillis(remainMillis));
  }

  @Test
  @DisplayName("sendPatchPwdEmail: 이메일 전송 성공")
  void sendPatchPwdEmail_성공() throws Exception {
    // given
    String email = "test@example.com";
    String name = "홍길동";
    CheckEmailRequest request = new CheckEmailRequest(email, name);

    Mockito.when(userRepository.findStaffForGetPwd(name, email))
        .thenReturn(Optional.of(Staff.builder().build()));
    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(valueOperations.get(email)).thenReturn(null); // 중복 발송 X

    // when
    authService.sendPatchPwdEmail(request);

    // then
    Mockito.verify(mailService).sendFindPwdEmail(eq(email), anyString());
    Mockito.verify(valueOperations).set(eq(email), anyString(), any(Duration.class));
  }

  @Test
  @DisplayName("sendPatchPwdEmail: 유저 정보가 없으면 예외 발생")
  void sendPatchPwdEmail_유저없음_예외() {
    // given
    String email = "ghost@example.com";
    CheckEmailRequest request = new CheckEmailRequest(email, "고스트");

    Mockito.when(userRepository.findStaffForGetPwd("고스트", email)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex =
        assertThrows(BusinessException.class, () -> authService.sendPatchPwdEmail(request));
    assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  @DisplayName("sendPatchPwdEmail: 이미 인증 코드가 있으면 예외 발생")
  void sendPatchPwdEmail_중복_예외() {
    // given
    String email = "test@example.com";
    CheckEmailRequest request = new CheckEmailRequest(email, "홍길동");

    Mockito.when(userRepository.findStaffForGetPwd("홍길동", email))
        .thenReturn(Optional.of(Staff.builder().build()));
    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(valueOperations.get(email)).thenReturn("123456"); // 이미 있음

    // when & then
    BusinessException ex =
        assertThrows(BusinessException.class, () -> authService.sendPatchPwdEmail(request));
    assertEquals(ErrorCode.DUPLICATE_SEND_AUTH_EXCEPTION, ex.getErrorCode());
  }

  @Test
  @DisplayName("sendPatchPwdEmail: 이메일 전송 실패 시 예외 발생")
  void sendPatchPwdEmail_이메일전송실패_예외() throws Exception {
    // given
    String email = "fail@example.com";
    CheckEmailRequest request = new CheckEmailRequest(email, "홍길동");

    Mockito.when(userRepository.findStaffForGetPwd("홍길동", email))
        .thenReturn(Optional.of(Staff.builder().build()));
    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(valueOperations.get(email)).thenReturn(null);

    Mockito.doThrow(MessagingException.class)
        .when(mailService)
        .sendFindPwdEmail(eq(email), anyString());

    // when & then
    BusinessException ex =
        assertThrows(BusinessException.class, () -> authService.sendPatchPwdEmail(request));
    assertEquals(ErrorCode.SEND_EMAIL_FAILURE_EXCEPTION, ex.getErrorCode());
  }

  @Test
  @DisplayName("verifyAuthCode: 인증 코드 일치 시 통과")
  void verifyAuthCode_성공() {
    // given
    String email = "test@example.com";
    String authCode = "ABC123";
    EmailVerifyRequest request = new EmailVerifyRequest(email, authCode);

    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(valueOperations.get(email)).thenReturn(authCode);

    // when
    authService.verifyAuthCode(request);

    // then
    Mockito.verify(redisTemplate).delete(authCode);
  }

  @Test
  @DisplayName("verifyAuthCode: 인증 코드 불일치 시 예외 발생")
  void verifyAuthCode_불일치_예외() {
    // given
    String email = "test@example.com";
    EmailVerifyRequest request = new EmailVerifyRequest(email, "XYZ999");

    Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    Mockito.when(valueOperations.get(email)).thenReturn("ABC123");

    // when & then
    BusinessException ex =
        assertThrows(BusinessException.class, () -> authService.verifyAuthCode(request));
    assertEquals(ErrorCode.INVALID_AUTH_CODE, ex.getErrorCode());
  }
}
