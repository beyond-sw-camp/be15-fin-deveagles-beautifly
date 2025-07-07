package com.deveagles.be15_deveagles_be.features.users.command.application.service;

import com.deveagles.be15_deveagles_be.features.auth.command.domain.aggregate.AccessAuth;
import com.deveagles.be15_deveagles_be.features.auth.command.domain.aggregate.AccessList;
import com.deveagles.be15_deveagles_be.features.auth.command.repository.AccessAuthRepository;
import com.deveagles.be15_deveagles_be.features.auth.command.repository.AccessListRepository;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.CreateStaffRequest;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.response.StaffInfoResponse;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.StaffStatus;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffCommandServiceImpl implements StaffCommandService {

  private final UserRepository userRepository;
  private final AccessListRepository accessListRepository;
  private final AccessAuthRepository accessAuthRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserCommandService userCommandService;

  @Override
  @Transactional
  public void staffCreate(Long shopId, CreateStaffRequest request, MultipartFile profile) {

    String profileUrl = null;

    if (profile != null && !profile.isEmpty()) {
      profileUrl = userCommandService.saveProfile(profile);
    }

    Staff staff =
        Staff.builder()
            .loginId(request.loginId())
            .password(passwordEncoder.encode(request.password()))
            .staffName(request.staffName())
            .grade(request.grade())
            .email(request.email())
            .shopId(shopId)
            .phoneNumber(request.phoneNumber())
            .colorCode("#364f6b")
            .staffStatus(StaffStatus.STAFF)
            .profileUrl(profileUrl)
            .build();

    Staff newStaff = userRepository.save(staff);

    List<AccessList> accessList = accessListRepository.findAll();

    for (AccessList access : accessList) {
      AccessAuth auth =
          AccessAuth.builder()
              .staffId(newStaff.getStaffId())
              .accessId(access.getAccessId())
              .canRead(true)
              .canWrite(false)
              .canDelete(false)
              .isActive(true)
              .build();

      accessAuthRepository.save(auth);
    }
  }

  private StaffInfoResponse buildStaffInfoResponse(Staff staff, List<AccessAuth> authList) {

    boolean isWorking = staff.getLeftDate() == null;

    return StaffInfoResponse.builder()
        .staffName(staff.getStaffName())
        .email(staff.getEmail())
        .grade(staff.getGrade())
        .colorCode(staff.getColorCode())
        .isWorking(isWorking)
        .joinedDate(staff.getJoinedDate())
        .phoneNumber(staff.getPhoneNumber())
        .description(staff.getStaffDescription())
        .accessList(authList)
        .build();
  }
}
