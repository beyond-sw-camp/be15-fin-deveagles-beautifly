package com.deveagles.be15_deveagles_be.features.users.command.application.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

  private final UserRepository userRepository;
  private final ModelMapper modelMapper;
  private final PasswordEncoder passwordEncoder;

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

    Staff findStaff =
        userRepository
            .findStaffByStaffId(request.staffId())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    return buildAccountResponse(findStaff);
  }

  @Override
  public AccountResponse patchAccount(PatchAccountRequest request) {

    Staff findStaff =
        userRepository
            .findStaffByStaffId(request.staffId())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    if (!request.email().isEmpty()) findStaff.patchEmail(request.email());
    if (!request.phoneNumber().isEmpty()) findStaff.patchPhoneNumber(request.phoneNumber());
    if (!request.password().isEmpty())
      findStaff.setEncodedPassword(passwordEncoder.encode(request.password()));

    Staff staff = userRepository.save(findStaff);

    return buildAccountResponse(staff);
  }

  private AccountResponse buildAccountResponse(Staff staff) {

    return AccountResponse.builder()
        .phoneNumber(staff.getPhoneNumber())
        .email(staff.getEmail())
        .build();
  }
}
