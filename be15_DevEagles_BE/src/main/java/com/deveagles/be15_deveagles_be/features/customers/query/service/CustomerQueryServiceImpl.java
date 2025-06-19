package com.deveagles.be15_deveagles_be.features.customers.query.service;

import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.CustomerResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository.CustomerJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CustomerQueryServiceImpl implements CustomerQueryService {

  private final CustomerJpaRepository customerJpaRepository;

  @Override
  public Optional<CustomerResponse> getCustomerById(Long customerId, Long shopId) {
    return customerJpaRepository
        .findByIdAndShopIdAndDeletedAtIsNull(customerId, shopId)
        .map(CustomerResponse::from);
  }

  @Override
  public Optional<CustomerResponse> getCustomerByPhoneNumber(String phoneNumber, Long shopId) {
    return customerJpaRepository
        .findByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId)
        .map(CustomerResponse::from);
  }

  @Override
  public List<CustomerResponse> getCustomersByShopId(Long shopId) {
    return customerJpaRepository.findByShopIdAndDeletedAtIsNull(shopId).stream()
        .map(CustomerResponse::from)
        .collect(Collectors.toList());
  }

  @Override
  public long getCustomerCountByShopId(Long shopId) {
    return customerJpaRepository.countByShopIdAndDeletedAtIsNull(shopId);
  }

  @Override
  public boolean existsByPhoneNumber(String phoneNumber, Long shopId) {
    return customerJpaRepository.existsByPhoneNumberAndShopIdAndDeletedAtIsNull(
        phoneNumber, shopId);
  }
}
