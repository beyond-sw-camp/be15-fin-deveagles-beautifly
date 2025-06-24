package com.deveagles.be15_deveagles_be.features.customers.query.infrastructure.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.CustomerGradeResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.CustomerGrade;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.CustomerGradeRepository;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerGradeQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CustomerGradeQueryServiceImpl implements CustomerGradeQueryService {

  private final CustomerGradeRepository customerGradeRepository;

  @Override
  public CustomerGradeResponse getCustomerGrade(Long customerGradeId, Long shopId) {
    log.info("고객등급 조회 요청 - 고객등급ID: {}, 매장ID: {}", customerGradeId, shopId);

    CustomerGrade customerGrade =
        customerGradeRepository
            .findByIdAndShopId(customerGradeId, shopId)
            .orElseThrow(
                () -> {
                  log.error("고객등급을 찾을 수 없음 - 고객등급ID: {}, 매장ID: {}", customerGradeId, shopId);
                  return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "고객등급을 찾을 수 없습니다.");
                });

    CustomerGradeResponse response =
        CustomerGradeResponse.builder()
            .id(customerGrade.getId())
            .shopId(customerGrade.getShopId())
            .customerGradeName(customerGrade.getCustomerGradeName())
            .discountRate(customerGrade.getDiscountRate())
            .build();

    log.info("고객등급 조회 완료 - 고객등급ID: {}", customerGradeId);
    return response;
  }

  @Override
  public List<CustomerGradeResponse> getAllCustomerGrades() {
    log.info("전체 고객등급 목록 조회 요청");

    List<CustomerGrade> customerGrades = customerGradeRepository.findAll();
    List<CustomerGradeResponse> responses =
        customerGrades.stream().map(this::mapToResponse).toList();

    log.info("전체 고객등급 목록 조회 완료 - 총 {}개", responses.size());
    return responses;
  }

  @Override
  public List<CustomerGradeResponse> getAllCustomerGradesByShopId(Long shopId) {
    log.info("매장별 고객등급 목록 조회 요청 - 매장ID: {}", shopId);

    List<CustomerGrade> customerGrades = customerGradeRepository.findByShopId(shopId);
    List<CustomerGradeResponse> responses =
        customerGrades.stream().map(this::mapToResponse).toList();

    log.info("매장별 고객등급 목록 조회 완료 - 매장ID: {}, 총 {}개", shopId, responses.size());
    return responses;
  }

  @Override
  public Page<CustomerGradeResponse> getCustomerGrades(Pageable pageable) {
    log.info("고객등급 페이징 조회 요청 - 페이지: {}, 크기: {}", pageable.getPageNumber(), pageable.getPageSize());

    Page<CustomerGrade> customerGrades = customerGradeRepository.findAll(pageable);
    Page<CustomerGradeResponse> responses = customerGrades.map(this::mapToResponse);

    log.info(
        "고객등급 페이징 조회 완료 - 현재페이지: {}, 전체페이지: {}, 전체개수: {}",
        responses.getNumber(),
        responses.getTotalPages(),
        responses.getTotalElements());

    return responses;
  }

  private CustomerGradeResponse mapToResponse(CustomerGrade customerGrade) {
    return CustomerGradeResponse.builder()
        .id(customerGrade.getId())
        .shopId(customerGrade.getShopId())
        .customerGradeName(customerGrade.getCustomerGradeName())
        .discountRate(customerGrade.getDiscountRate())
        .build();
  }
}
