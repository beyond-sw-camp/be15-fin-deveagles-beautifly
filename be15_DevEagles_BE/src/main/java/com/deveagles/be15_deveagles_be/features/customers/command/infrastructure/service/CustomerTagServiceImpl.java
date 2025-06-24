package com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.command.application.service.CustomerTagService;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.TagByCustomer;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.CustomerRepository;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.TagByCustomerRepository;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerTagServiceImpl implements CustomerTagService {

  private final TagByCustomerRepository tagByCustomerRepository;
  private final CustomerRepository customerRepository;
  private final TagRepository tagRepository;

  @Override
  public void addTagToCustomer(Long customerId, Long tagId, Long shopId) {
    log.info("고객 태그 추가 요청 - 고객ID: {}, 태그ID: {}, 매장ID: {}", customerId, tagId, shopId);

    validateCustomerExists(customerId, shopId);
    validateTagExists(tagId, shopId);

    if (tagByCustomerRepository.existsByCustomerIdAndTagId(customerId, tagId)) {
      log.error("이미 할당된 태그 - 고객ID: {}, 태그ID: {}", customerId, tagId);
      throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 고객에게 할당된 태그입니다.");
    }

    TagByCustomer tagByCustomer =
        TagByCustomer.builder().customerId(customerId).tagId(tagId).build();

    tagByCustomerRepository.save(tagByCustomer);

    log.info("고객 태그 추가 완료 - 고객ID: {}, 태그ID: {}", customerId, tagId);
  }

  @Override
  public void removeTagFromCustomer(Long customerId, Long tagId, Long shopId) {
    log.info("고객 태그 제거 요청 - 고객ID: {}, 태그ID: {}, 매장ID: {}", customerId, tagId, shopId);

    validateCustomerExists(customerId, shopId);

    if (!tagByCustomerRepository.existsByCustomerIdAndTagId(customerId, tagId)) {
      log.error("할당되지 않은 태그 제거 시도 - 고객ID: {}, 태그ID: {}", customerId, tagId);
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "고객에게 할당되지 않은 태그입니다.");
    }

    tagByCustomerRepository.deleteByCustomerIdAndTagId(customerId, tagId);

    log.info("고객 태그 제거 완료 - 고객ID: {}, 태그ID: {}", customerId, tagId);
  }

  private void validateCustomerExists(Long customerId, Long shopId) {
    if (customerRepository.findByIdAndShopId(customerId, shopId).isEmpty()) {
      log.error("고객을 찾을 수 없음 - 고객ID: {}, 매장ID: {}", customerId, shopId);
      throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND, "고객을 찾을 수 없습니다.");
    }
  }

  private void validateTagExists(Long tagId, Long shopId) {
    if (!tagRepository.existsByIdAndShopId(tagId, shopId)) {
      log.error("태그를 찾을 수 없음 - 태그ID: {}, 매장ID: {}", tagId, shopId);
      throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "태그를 찾을 수 없습니다.");
    }
  }
}
