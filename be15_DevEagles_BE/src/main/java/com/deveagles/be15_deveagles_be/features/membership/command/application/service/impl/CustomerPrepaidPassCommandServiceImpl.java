package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerPrepaidPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerPrepaidPassUpdateRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.CustomerPrepaidPassCommandService;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.CustomerPrepaidPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerPrepaidPassRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerPrepaidPassCommandServiceImpl implements CustomerPrepaidPassCommandService {

  private final CustomerPrepaidPassRepository customerPrepaidPassRepository;

  @Override
  public void registCustomerPrepaidPass(CustomerPrepaidPassRegistRequest request) {

    CustomerPrepaidPass customerPrepaidPass =
        CustomerPrepaidPass.builder()
            .customerId(request.getCustomerId())
            .prepaidPassId(request.getPrepaidPassId())
            .remainingAmount(request.getRemainingAmount())
            .expirationDate(request.getExpirationDate())
            .build();

    customerPrepaidPassRepository.save(customerPrepaidPass);
  }

  @Override
  public void updateCustomerPrepaidPass(CustomerPrepaidPassUpdateRequest request) {
    CustomerPrepaidPass pass =
        customerPrepaidPassRepository
            .findById(request.getCustomerPrepaidPassId())
            .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMERPREPAIDPASS_NOT_FOUND));

    // 3. 값 유효성 체크 및 필드 수정
    if (request.getRemainingAmount() != null) {
      pass.setRemainingAmount(request.getRemainingAmount());
    }

    if (request.getExpirationDate() != null) {
      pass.setExpirationDate(request.getExpirationDate());
    }

    // 4. 수정 시간 갱신
    pass.setModifiedAt(LocalDateTime.now());

    // 5. 저장
    customerPrepaidPassRepository.save(pass);
  }
}
