package com.deveagles.be15_deveagles_be.features.users.command.application.service;

import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.auth.command.domain.aggregate.AccessAuth;
import com.deveagles.be15_deveagles_be.features.auth.command.domain.aggregate.AccessList;
import com.deveagles.be15_deveagles_be.features.auth.command.repository.AccessAuthRepository;
import com.deveagles.be15_deveagles_be.features.auth.command.repository.AccessListRepository;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.CreateStaffRequest;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("StaffCommandService 단위 테스트")
public class StaffCommandServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private AccessListRepository accessListRepository;
  @Mock private AccessAuthRepository accessAuthRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserCommandService userCommandService;
  @Mock private MultipartFile profile;

  private StaffCommandServiceImpl staffCommandService;

  @BeforeEach
  void setUp() {
    staffCommandService =
        new StaffCommandServiceImpl(
            userRepository,
            accessListRepository,
            accessAuthRepository,
            passwordEncoder,
            userCommandService);
  }

  @Test
  @DisplayName("staffCreate: 프로필 없이 직원 생성 성공")
  void staffCreate_프로필없음_성공() {
    // given
    Long shopId = 1L;
    CreateStaffRequest request =
        new CreateStaffRequest(
            "홍길동", // staffName
            "staff01", // loginId
            "test@example.com", // email
            "1234", // password
            "01012345678", // phoneNumber
            "매니저" // grade
            );

    Mockito.when(passwordEncoder.encode("1234")).thenReturn("encodedPw");

    Staff savedStaff = Staff.builder().staffId(1L).loginId("staff01").build();

    Mockito.when(userRepository.save(Mockito.any())).thenReturn(savedStaff);
    Mockito.when(accessListRepository.findAll())
        .thenReturn(
            List.of(
                AccessList.builder().accessId(1L).build(),
                AccessList.builder().accessId(2L).build()));

    // when
    staffCommandService.staffCreate(shopId, request, null);

    // then
    verify(userRepository).save(Mockito.any(Staff.class));
    verify(accessAuthRepository, times(2)).save(Mockito.any(AccessAuth.class));
  }

  @Test
  @DisplayName("staffCreate: 프로필이 있을 때 직원 생성 성공")
  void staffCreate_프로필있음_성공() {
    // given
    Long shopId = 1L;
    CreateStaffRequest request =
        new CreateStaffRequest("이순신", "staff02", "lee@example.com", "5678", "01098765432", "부점장");

    Mockito.when(profile.isEmpty()).thenReturn(false);
    Mockito.when(userCommandService.saveProfile(profile))
        .thenReturn("https://s3.bucket/profile.jpg");
    Mockito.when(passwordEncoder.encode("5678")).thenReturn("encodedPw");

    Staff savedStaff = Staff.builder().staffId(2L).loginId("staff02").build();

    Mockito.when(userRepository.save(Mockito.any())).thenReturn(savedStaff);
    Mockito.when(accessListRepository.findAll())
        .thenReturn(List.of(AccessList.builder().accessId(1L).build()));

    // when
    staffCommandService.staffCreate(shopId, request, profile);

    // then
    verify(userCommandService).saveProfile(profile);
    verify(userRepository).save(Mockito.any(Staff.class));
    verify(accessAuthRepository).save(Mockito.any(AccessAuth.class));
  }
}
