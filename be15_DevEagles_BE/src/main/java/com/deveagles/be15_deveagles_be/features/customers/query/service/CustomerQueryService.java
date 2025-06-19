package com.deveagles.be15_deveagles_be.features.customers.query.service;

import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.CustomerResponse;
import java.util.List;
import java.util.Optional;

public interface CustomerQueryService {

  // 상세 조회 (단건)
  Optional<CustomerResponse> getCustomerById(Long customerId, Long shopId);

  Optional<CustomerResponse> getCustomerByPhoneNumber(String phoneNumber, Long shopId);

  // 전체 목록 조회 (상세 정보 포함)
  List<CustomerResponse> getCustomersByShopId(Long shopId);

  // 통계/집계 정보
  long getCustomerCountByShopId(Long shopId);

  boolean existsByPhoneNumber(String phoneNumber, Long shopId);
}
