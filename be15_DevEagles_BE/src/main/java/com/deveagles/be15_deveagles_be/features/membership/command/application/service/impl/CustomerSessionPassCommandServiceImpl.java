package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerSessionPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerSessionPassUpdateRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.CustomerSessionPassCommandService;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerSessionPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerSessionPassRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerSessionPassCommandServiceImpl implements CustomerSessionPassCommandService {

  private final CustomerSessionPassRepository customerSessionPassRepository;

  @Override
  public void registCustomerSessionPass(CustomerSessionPassRegistRequest request) {

    CustomerSessionPass customerSessionPass =
        CustomerSessionPass.builder()
            .customerId(request.getCustomerId())
            .sessionPassId(request.getSessionPassId())
            .remainingCount(request.getRemainingCount())
            .expirationDate(request.getExpirationDate())
            .build();

    customerSessionPassRepository.save(customerSessionPass);
  }

  @Override
  public void updateCustomerSessionPass(CustomerSessionPassUpdateRequest request) {
    CustomerSessionPass pass =
        customerSessionPassRepository
            .findById(request.getCustomerSessionPassId())
            .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMERSESSIONPASS_NOT_FOUND));

    // 3. 값 유효성 체크 및 필드 수정
    if (request.getRemainingCount() != null) {
      pass.setRemainingCount(request.getRemainingCount());
    }

    if (request.getExpirationDate() != null) {
      pass.setExpirationDate(request.getExpirationDate());
    }

    // 4. 수정 시간 갱신
    pass.setModifiedAt(LocalDateTime.now());

    // 5. 저장
    customerSessionPassRepository.save(pass);
  }
}
