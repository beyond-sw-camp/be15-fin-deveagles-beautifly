package com.deveagles.be15_deveagles_be.features.messages.query.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.messages.query.dto.response.SmsDetailResponse;
import com.deveagles.be15_deveagles_be.features.messages.query.dto.response.SmsListResponse;
import org.springframework.data.domain.Pageable;

public interface SmsQueryService {
  PagedResult<SmsListResponse> getSmsList(Long shopId, Pageable pageable);

  SmsDetailResponse getSmsDetail(Long shopId, Long messageId);
}
