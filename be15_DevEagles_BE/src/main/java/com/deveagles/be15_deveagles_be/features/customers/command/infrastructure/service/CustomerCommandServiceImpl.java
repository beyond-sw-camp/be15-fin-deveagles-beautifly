package com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateCustomerRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateCustomerRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.CustomerCommandResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.application.service.CustomerCommandService;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.CustomerRepository;
import com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository.CustomerJpaRepository;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerQueryService;
import com.deveagles.be15_deveagles_be.features.messages.command.application.service.AutomaticMessageTriggerService;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.AutomaticEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerCommandServiceImpl implements CustomerCommandService {

  private final CustomerRepository customerRepository;
  private final CustomerQueryService customerQueryService;
  private final CustomerJpaRepository customerJpaRepository;
  private final AutomaticMessageTriggerService automaticMessageTriggerService;

  @Override
  public CustomerCommandResponse createCustomer(CreateCustomerRequest request) {
    Customer customer =
        Customer.builder()
            .customerGradeId(request.customerGradeId())
            .shopId(request.shopId())
            .staffId(request.staffId())
            .customerName(request.customerName())
            .phoneNumber(request.phoneNumber())
            .memo(request.memo())
            .birthdate(request.birthdate())
            .gender(request.gender())
            .marketingConsent(request.marketingConsent())
            .notificationConsent(request.notificationConsent())
            .channelId(request.channelId())
            .build();

    if (Boolean.TRUE.equals(request.marketingConsent())) {
      customer.updateMarketingConsent(true);
    }

    Customer savedCustomer = customerRepository.save(customer);
    customerJpaRepository.flush();

    // 3. 자동발신 트리거 실행
    automaticMessageTriggerService.triggerAutomaticSend(
        savedCustomer, AutomaticEventType.NEW_CUSTOMER);
    // Elasticsearch 동기화
    customerQueryService.syncCustomerToElasticsearch(savedCustomer.getId());

    log.info(
        "새 고객 생성됨: ID={}, 매장ID={}, 이름={}",
        savedCustomer.getId(),
        savedCustomer.getShopId(),
        savedCustomer.getCustomerName());

    return CustomerCommandResponse.from(savedCustomer);
  }

  @Override
  public CustomerCommandResponse updateCustomer(UpdateCustomerRequest request) {
    Customer customer =
        customerRepository
            .findByIdAndShopId(request.customerId(), getCurrentShopId())
            .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

    customer.updateCustomerInfo(
        request.customerName(),
        request.phoneNumber(),
        request.memo(),
        request.gender(),
        request.channelId());

    Customer updatedCustomer = customerRepository.save(customer);

    // Elasticsearch 동기화
    customerQueryService.syncCustomerToElasticsearch(updatedCustomer.getId());

    log.info("고객 정보 수정됨: ID={}, 이름={}", updatedCustomer.getId(), updatedCustomer.getCustomerName());

    return CustomerCommandResponse.from(updatedCustomer);
  }

  @Override
  public void deleteCustomer(Long customerId, Long shopId) {
    Customer customer =
        customerRepository
            .findByIdAndShopId(customerId, shopId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

    customer.softDelete();
    customerRepository.save(customer);
    log.info("고객 삭제됨: ID={}, 매장ID={}", customerId, shopId);

    // Elasticsearch 동기화
    customerQueryService.syncCustomerToElasticsearch(customerId);
  }

  @Override
  public CustomerCommandResponse updateMarketingConsent(
      Long customerId, Long shopId, Boolean consent) {
    Customer customer =
        customerRepository
            .findByIdAndShopId(customerId, shopId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

    customer.updateMarketingConsent(consent);
    Customer updatedCustomer = customerRepository.save(customer);

    return CustomerCommandResponse.from(updatedCustomer);
  }

  @Override
  public CustomerCommandResponse updateNotificationConsent(
      Long customerId, Long shopId, Boolean consent) {
    Customer customer =
        customerRepository
            .findByIdAndShopId(customerId, shopId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

    customer.updateNotificationConsent(consent);
    Customer updatedCustomer = customerRepository.save(customer);

    return CustomerCommandResponse.from(updatedCustomer);
  }

  @Override
  public CustomerCommandResponse addVisit(Long customerId, Long shopId, Integer revenue) {
    Customer customer =
        customerRepository
            .findByIdAndShopId(customerId, shopId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

    customer.addVisit(revenue);
    Customer updatedCustomer = customerRepository.save(customer);
    log.info("고객 방문 추가됨: ID={}, 매출={}", customerId, revenue);

    return CustomerCommandResponse.from(updatedCustomer);
  }

  @Override
  public CustomerCommandResponse addNoshow(Long customerId, Long shopId) {
    Customer customer =
        customerRepository
            .findByIdAndShopId(customerId, shopId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

    customer.addNoshow();
    Customer updatedCustomer = customerRepository.save(customer);
    log.info("고객 노쇼 추가됨: ID={}", customerId);

    return CustomerCommandResponse.from(updatedCustomer);
  }

  // TODO: 실제 구현에서는 SecurityContext에서 shopId 가져오기
  private Long getCurrentShopId() {
    return 1L; // 임시값
  }
}
