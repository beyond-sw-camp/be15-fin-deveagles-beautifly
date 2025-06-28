package com.deveagles.be15_deveagles_be.features.auth.command.application.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request.LoginRequest;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void login(LoginRequest request) {

    Staff staff =
        userRepository
            .findStaffByLoginId(request.username())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NAME_NOT_FOUND));

    if (passwordEncoder.matches(request.password(), staff.getPassword())) {
      throw new BusinessException(ErrorCode.USER_INVALID_LOGIN);
    }

    // todo : 토큰 발급

  }
}
