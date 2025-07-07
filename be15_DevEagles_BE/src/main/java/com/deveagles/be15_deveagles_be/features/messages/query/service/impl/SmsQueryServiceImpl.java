package com.deveagles.be15_deveagles_be.features.messages.query.service.impl;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.messages.query.dto.response.SmsDetailResponse;
import com.deveagles.be15_deveagles_be.features.messages.query.dto.response.SmsListResponse;
import com.deveagles.be15_deveagles_be.features.messages.query.repository.SmsQueryRepository;
import com.deveagles.be15_deveagles_be.features.messages.query.service.SmsQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsQueryServiceImpl implements SmsQueryService {

  private final SmsQueryRepository smsQueryRepository;

  @Override
  public PagedResult<SmsListResponse> getSmsList(Long shopId, Pageable pageable) {
    Page<SmsListResponse> page = smsQueryRepository.findSmsListByShopId(shopId, pageable);
    return PagedResult.from(page);
  }

  @Override
  public SmsDetailResponse getSmsDetail(Long shopId, Long messageId) {
    return smsQueryRepository
        .findSmsDetailByIdAndShopId(messageId, shopId)
        .orElseThrow(() -> new BusinessException(ErrorCode.SMS_NOT_FOUND));
  }
}
