package com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateCustomerGradeRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateCustomerGradeRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.service.CustomerGradeCommandService;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.CustomerGrade;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.CustomerGradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerGradeCommandServiceImpl implements CustomerGradeCommandService {

  private final CustomerGradeRepository customerGradeRepository;

  @Override
  public Long createCustomerGrade(CreateCustomerGradeRequest request) {
    log.info(
        "고객등급 생성 요청 - 매장ID: {}, 등급명: {}, 할인율: {}%",
        request.getShopId(), request.getCustomerGradeName(), request.getDiscountRate());

    validateGradeNameNotExists(request.getCustomerGradeName(), request.getShopId());

    CustomerGrade customerGrade =
        CustomerGrade.builder()
            .shopId(request.getShopId())
            .customerGradeName(request.getCustomerGradeName())
            .discountRate(request.getDiscountRate())
            .build();

    CustomerGrade savedGrade = customerGradeRepository.save(customerGrade);

    log.info(
        "고객등급 생성 완료 - ID: {}, 매장ID: {}, 등급명: {}, 할인율: {}%",
        savedGrade.getId(),
        savedGrade.getShopId(),
        savedGrade.getCustomerGradeName(),
        savedGrade.getDiscountRate());
    return savedGrade.getId();
  }

  @Override
  public void updateCustomerGrade(Long gradeId, UpdateCustomerGradeRequest request) {
    log.info(
        "고객등급 수정 요청 - ID: {}, 매장ID: {}, 새 등급명: {}, 새 할인율: {}%",
        gradeId, request.getShopId(), request.getCustomerGradeName(), request.getDiscountRate());

    CustomerGrade customerGrade = findCustomerGradeByIdAndShopId(gradeId, request.getShopId());

    if (!customerGrade.getCustomerGradeName().equals(request.getCustomerGradeName())) {
      validateGradeNameNotExists(request.getCustomerGradeName(), request.getShopId());
    }

    String oldGradeName = customerGrade.getCustomerGradeName();
    Integer oldDiscountRate = customerGrade.getDiscountRate();

    customerGrade.updateGradeName(request.getCustomerGradeName());
    customerGrade.updateDiscountRate(request.getDiscountRate());

    log.info(
        "고객등급 수정 완료 - ID: {}, 매장ID: {}, 등급명: {} -> {}, 할인율: {}% -> {}%",
        gradeId,
        request.getShopId(),
        oldGradeName,
        request.getCustomerGradeName(),
        oldDiscountRate,
        request.getDiscountRate());
  }

  @Override
  public void deleteCustomerGrade(Long gradeId) {
    log.info("고객등급 삭제 요청 - ID: {}", gradeId);

    CustomerGrade customerGrade = findCustomerGradeById(gradeId);
    customerGradeRepository.delete(customerGrade);

    log.info("고객등급 삭제 완료 - ID: {}, 등급명: {}", gradeId, customerGrade.getCustomerGradeName());
  }

  private CustomerGrade findCustomerGradeById(Long gradeId) {
    return customerGradeRepository
        .findById(gradeId)
        .orElseThrow(
            () -> {
              log.error("고객등급을 찾을 수 없음 - ID: {}", gradeId);
              return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "고객등급을 찾을 수 없습니다.");
            });
  }

  private CustomerGrade findCustomerGradeByIdAndShopId(Long gradeId, Long shopId) {
    return customerGradeRepository
        .findByIdAndShopId(gradeId, shopId)
        .orElseThrow(
            () -> {
              log.error("고객등급을 찾을 수 없음 - ID: {}, 매장ID: {}", gradeId, shopId);
              return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "고객등급을 찾을 수 없습니다.");
            });
  }

  private void validateGradeNameNotExists(String customerGradeName, Long shopId) {
    if (customerGradeRepository.existsByCustomerGradeNameAndShopId(customerGradeName, shopId)) {
      log.error("중복된 고객등급명 - 등급명: {}, 매장ID: {}", customerGradeName, shopId);
      throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 고객등급명입니다.");
    }
  }
}
