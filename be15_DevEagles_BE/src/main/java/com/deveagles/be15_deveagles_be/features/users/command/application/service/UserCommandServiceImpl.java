package com.deveagles.be15_deveagles_be.features.users.command.application.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.*;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.response.AccountResponse;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.response.ProfileResponse;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.StaffStatus;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AmazonS3 amazonS3;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Override
  public Staff userRegist(UserCreateRequest request, Long shopId) {

    Staff staff =
        Staff.builder()
            .loginId(request.loginId())
            .password(passwordEncoder.encode(request.password()))
            .staffName(request.staffName())
            .grade("점장")
            .email(request.email())
            .shopId(shopId)
            .phoneNumber(request.phoneNumber())
            .colorCode("#364f6b")
            .staffStatus(StaffStatus.STAFF)
            .build();

    return userRepository.save(staff);
  }

  @Override
  public Boolean validCheckId(ValidCheckRequest validRequest) {

    Optional<Staff> findStaff = userRepository.findStaffByLoginId(validRequest.checkItem());

    if (findStaff.isEmpty()) return Boolean.TRUE;
    else return Boolean.FALSE;
  }

  @Override
  public Boolean validCheckEmail(ValidCheckRequest validRequest) {
    Optional<Staff> findStaff = userRepository.findStaffByEmail(validRequest.checkItem());

    if (findStaff.isEmpty()) return Boolean.TRUE;
    else return Boolean.FALSE;
  }

  @Override
  public AccountResponse getAccount(GetAccountRequest request) {

    Staff findStaff = findStaffByStaffId(request.staffId());

    return buildAccountResponse(findStaff);
  }

  @Override
  public AccountResponse patchAccount(PatchAccountRequest request) {

    Staff findStaff = findStaffByStaffId(request.staffId());

    if (!request.email().isEmpty()) findStaff.patchEmail(request.email());
    if (!request.phoneNumber().isEmpty()) findStaff.patchPhoneNumber(request.phoneNumber());
    if (!request.password().isEmpty())
      findStaff.setEncodedPassword(passwordEncoder.encode(request.password()));

    Staff staff = userRepository.save(findStaff);

    return buildAccountResponse(staff);
  }

  @Override
  public ProfileResponse getProfile(CustomUser customUser) {

    Staff findStaff = findStaffByStaffId(customUser.getUserId());

    return buildProfileResponse(findStaff);
  }

  @Override
  public ProfileResponse patchProfile(
      Long staffId, PatchProfileRequest request, MultipartFile profile) {

    Staff findStaff = findStaffByStaffId(staffId);

    if (profile != null) {
      if (!profile.isEmpty()) {
        String profileUrl = saveProfile(profile);
        findStaff.patchProfileUrl(profileUrl);
      } else {
        findStaff.patchProfileUrl(null);
      }
    }

    if (!request.colorCode().isEmpty()) findStaff.patchColorCode(request.colorCode());
    if (!request.description().isEmpty()) findStaff.patchDescription(request.description());
    if (!request.staffName().isEmpty()) findStaff.patchName(request.staffName());
    if (!request.grade().isEmpty()) findStaff.patchGrade(request.grade());

    return buildProfileResponse(userRepository.save(findStaff));
  }

  private Staff findStaffByStaffId(Long staffId) {

    return userRepository
        .findStaffByStaffId(staffId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
  }

  private AccountResponse buildAccountResponse(Staff staff) {

    return AccountResponse.builder()
        .phoneNumber(staff.getPhoneNumber())
        .email(staff.getEmail())
        .build();
  }

  private ProfileResponse buildProfileResponse(Staff staff) {

    return ProfileResponse.builder()
        .profileUrl(staff.getProfileUrl())
        .description(staff.getStaffDescription())
        .colorCode(staff.getColorCode())
        .build();
  }

  private String saveProfile(MultipartFile profile) {

    String fileName = "user/thumbnail_" + UUID.randomUUID() + "_" + profile.getOriginalFilename();

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(profile.getSize());
    metadata.setContentType(profile.getContentType());

    try {
      amazonS3.putObject(bucket, fileName, profile.getInputStream(), metadata);
    } catch (IOException e) {
      throw new BusinessException(ErrorCode.FILE_SAVE_ERROR);
    }

    return amazonS3.getUrl(bucket, fileName).toString();
  }
}
