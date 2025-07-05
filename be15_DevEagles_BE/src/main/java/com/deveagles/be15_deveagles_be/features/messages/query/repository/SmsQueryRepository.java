package com.deveagles.be15_deveagles_be.features.messages.query.repository;

import com.deveagles.be15_deveagles_be.features.messages.query.dto.response.SmsDetailResponse;
import com.deveagles.be15_deveagles_be.features.messages.query.dto.response.SmsListResponse;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SmsQueryRepository {
  Page<SmsListResponse> findSmsListByShopId(Long shopId, Pageable pageable);

  Optional<SmsDetailResponse> findSmsDetailByIdAndShopId(Long messageId, Long shopId); // ← 상세용
}
