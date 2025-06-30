package com.deveagles.be15_deveagles_be.features.users.command.application.service;

import static org.junit.jupiter.api.Assertions.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.GetAccountRequest;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.PatchAccountRequest;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.UserCreateRequest;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.ValidCheckRequest;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.response.AccountResponse;
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

  @Test
  @DisplayName("getAccount: 존재하는 staffId로 계정 정보를 반환한다")
  void getAccount_성공() {
    // given
    Long staffId = 1L;
    GetAccountRequest request = new GetAccountRequest(staffId);

    Staff staff =
        Staff.builder().staffId(staffId).email("test@naver.com").phoneNumber("01012345678").build();

    Mockito.when(userRepository.findStaffByStaffId(staffId)).thenReturn(Optional.of(staff));

    // when
    AccountResponse result = service.getAccount(request);

    // then
    assertEquals("test@naver.com", result.getEmail());
    assertEquals("01012345678", result.getPhoneNumber());
  }

  @Test
  @DisplayName("getAccount: 존재하지 않는 staffId면 예외를 반환한다")
  void getAccount_예외_테스트() {
    // given
    Long staffId = 99L;
    GetAccountRequest request = new GetAccountRequest(staffId);
    Mockito.when(userRepository.findStaffByStaffId(staffId)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex = assertThrows(BusinessException.class, () -> service.getAccount(request));
    assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  @DisplayName("patchAccount: 이메일, 전화번호, 비밀번호를 수정한다")
  void patchAccount_전체수정_테스트() {
    // given
    Long staffId = 1L;
    PatchAccountRequest request =
        new PatchAccountRequest(1L, "new@email.com", "01098765432", "newPassword");

    Staff staff =
        Staff.builder()
            .staffId(staffId)
            .email("old@email.com")
            .phoneNumber("01011112222")
            .password("encodedOldPw")
            .build();

    Mockito.when(userRepository.findStaffByStaffId(staffId)).thenReturn(Optional.of(staff));
    Mockito.when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPw");
    Mockito.when(userRepository.save(Mockito.any(Staff.class))).thenReturn(staff);

    // when
    AccountResponse response = service.patchAccount(request);

    // then
    assertEquals("new@email.com", response.getEmail());
    assertEquals("01098765432", response.getPhoneNumber());
    assertEquals("encodedNewPw", staff.getPassword());
  }

  @Test
  @DisplayName("patchAccount: 존재하지 않는 staffId면 예외 발생")
  void patchAccount_예외_테스트() {
    // given
    PatchAccountRequest request =
        new PatchAccountRequest(123L, "hi@email.com", "01000000000", "1234");

    Mockito.when(userRepository.findStaffByStaffId(123L)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex =
        assertThrows(BusinessException.class, () -> service.patchAccount(request));
    assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  @DisplayName("patchAccount: 이메일만 수정하는 경우")
  void patchAccount_이메일만_수정() {
    // given
    Long staffId = 1L;
    PatchAccountRequest request =
        new PatchAccountRequest(
            staffId,
            "new@email.com",
            "", // 전화번호 미입력
            "" // 비밀번호 미입력
            );

    Staff staff =
        Staff.builder()
            .staffId(staffId)
            .email("old@email.com")
            .phoneNumber("01011112222")
            .password("encodedOldPw")
            .build();

    Mockito.when(userRepository.findStaffByStaffId(staffId)).thenReturn(Optional.of(staff));
    Mockito.when(userRepository.save(Mockito.any(Staff.class))).thenReturn(staff);

    // when
    AccountResponse response = service.patchAccount(request);

    // then
    assertEquals("new@email.com", response.getEmail());
    assertEquals("01011112222", response.getPhoneNumber()); // 변경 없음
  }

  @Test
  @DisplayName("patchAccount: 전화번호만 수정하는 경우")
  void patchAccount_전화번호만_수정() {
    // given
    Long staffId = 1L;
    PatchAccountRequest request =
        new PatchAccountRequest(
            staffId,
            "", // 이메일 미입력
            "01099998888", // 전화번호만 수정
            "" // 비밀번호 미입력
            );

    Staff staff =
        Staff.builder()
            .staffId(staffId)
            .email("test@email.com")
            .phoneNumber("01012345678")
            .password("pw")
            .build();

    Mockito.when(userRepository.findStaffByStaffId(staffId)).thenReturn(Optional.of(staff));
    Mockito.when(userRepository.save(Mockito.any(Staff.class))).thenReturn(staff);

    // when
    AccountResponse response = service.patchAccount(request);

    // then
    assertEquals("test@email.com", response.getEmail()); // 변경 없음
    assertEquals("01099998888", response.getPhoneNumber());
  }

  @Test
  @DisplayName("patchAccount: 비밀번호만 수정하는 경우")
  void patchAccount_비밀번호만_수정() {
    // given
    Long staffId = 1L;
    PatchAccountRequest request =
        new PatchAccountRequest(
            staffId,
            "", // email
            "", // phoneNumber
            "newPassword123" // password
            );

    Staff staff =
        Staff.builder()
            .staffId(staffId)
            .email("test@email.com")
            .phoneNumber("01012345678")
            .password("oldEncodedPw")
            .build();

    Mockito.when(userRepository.findStaffByStaffId(staffId)).thenReturn(Optional.of(staff));
    Mockito.when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPw");
    Mockito.when(userRepository.save(Mockito.any(Staff.class))).thenReturn(staff);

    // when
    AccountResponse response = service.patchAccount(request);

    // then
    assertEquals("test@email.com", response.getEmail()); // 변경 없음
    assertEquals("01012345678", response.getPhoneNumber()); // 변경 없음
    assertEquals("encodedNewPw", staff.getPassword()); // 변경됨
  }
}
