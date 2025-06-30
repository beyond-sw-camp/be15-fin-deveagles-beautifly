package com.deveagles.be15_deveagles_be.features.auth.command.application.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    Staff staff =
        userRepository
            .findStaffByLoginId(username)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NAME_NOT_FOUND));

    return CustomUser.builder()
        .shopId(staff.getShopId())
        .userId(staff.getStaffId())
        .username(staff.getLoginId())
        .password(staff.getPassword())
        .staffStatus(staff.getStaffStatus())
        .staffName(staff.getStaffName())
        .grade(staff.getGrade())
        .profileUrl(staff.getProfileUrl())
        .build();
  }
}
