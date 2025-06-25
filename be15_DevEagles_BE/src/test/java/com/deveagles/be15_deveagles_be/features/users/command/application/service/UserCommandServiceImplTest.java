package com.deveagles.be15_deveagles_be.features.users.command.application.service;

import static org.junit.jupiter.api.Assertions.*;

import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.UserCreateRequest;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.ValidCheckRequest;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserCommandService 단위 테스트")
public class UserCommandServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private ModelMapper modelMapper;

  @Mock private PasswordEncoder passwordEncoder;

  private UserCommandServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new UserCommandServiceImpl(userRepository, modelMapper, passwordEncoder);
  }

  @Test
  @DisplayName("회원 로그인ID 중복 테스트")
  void 중복된_ID가_있으면_FALSE가_반환된다() {

    String loginId = "user01";
    ValidCheckRequest request = new ValidCheckRequest(loginId);

    Staff existingStaff = Staff.builder().loginId(loginId).build();
    Mockito.when(userRepository.findStaffByLoginId(loginId)).thenReturn(Optional.of(existingStaff));

    UserCommandServiceImpl service =
        new UserCommandServiceImpl(userRepository, modelMapper, passwordEncoder);

    // when
    Boolean result = service.validCheckId(request);

    // then
    assertFalse(result);
  }

  @Test
  @DisplayName("중복된 ID가 없으면 true 반환")
  void 중복된_ID가_없으면_TRUE가_반환된다() {
    // given
    String loginId = "newUser";
    ValidCheckRequest request = new ValidCheckRequest(loginId);

    Mockito.when(userRepository.findStaffByLoginId(loginId)).thenReturn(Optional.empty());

    UserCommandServiceImpl service =
        new UserCommandServiceImpl(userRepository, modelMapper, passwordEncoder);

    // when
    Boolean result = service.validCheckId(request);

    // then
    assertTrue(result);
  }

  @Test
  @DisplayName("validCheckEmail: 이메일 중복 시 false 반환")
  void 중복된_이메일이면_FALSE가_반환된다() {
    // given
    String email = "test@example.com";
    ValidCheckRequest request = new ValidCheckRequest(email);

    Staff existing = Staff.builder().email(email).build();
    Mockito.when(userRepository.findStaffByEmail(email)).thenReturn(Optional.of(existing));

    // when
    Boolean result = service.validCheckEmail(request);

    // then
    assertFalse(result);
  }

  @Test
  @DisplayName("validCheckEmail: 이메일 사용 가능하면 true 반환")
  void 사용가능한_이메일이면_TRUE가_반환된다() {
    // given
    String email = "available@example.com";
    ValidCheckRequest request = new ValidCheckRequest(email);

    Mockito.when(userRepository.findStaffByEmail(email)).thenReturn(Optional.empty());

    // when
    Boolean result = service.validCheckEmail(request);

    // then
    assertTrue(result);
  }

  @Test
  @DisplayName("userRegist: 사용자 등록 테스트")
  void userRegist_요청으로_Staff가_생성된다() {
    // given
    UserCreateRequest request =
        UserCreateRequest.builder()
            .loginId("user01")
            .password("rawPassword")
            .staffName("홍길동")
            .email("hong@example.com")
            .phoneNumber("01012345678")
            .build();

    Long shopId = 1L;

    Mockito.when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPw");

    Staff savedStaff =
        Staff.builder()
            .loginId("user01")
            .password("encodedPw")
            .staffName("홍길동")
            .grade("점장")
            .email("hong@example.com")
            .shopId(shopId)
            .phoneNumber("01012345678")
            .build();

    Mockito.when(userRepository.save(Mockito.any(Staff.class))).thenReturn(savedStaff);

    // when
    Staff result = service.userRegist(request, shopId);

    // then
    assertEquals("user01", result.getLoginId());
    assertEquals("encodedPw", result.getPassword());
    assertEquals("홍길동", result.getStaffName());
    assertEquals("hong@example.com", result.getEmail());
    assertEquals("01012345678", result.getPhoneNumber());
  }
}
