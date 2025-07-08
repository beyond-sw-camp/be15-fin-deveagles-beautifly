package com.deveagles.be15_deveagles_be.features.customers.query.infrastructure.service;

import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QCustomer.customer;
import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QCustomerGrade.customerGrade;
import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QTag.tag;
import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QTagByCustomer.tagByCustomer;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.TagResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.CustomerRepository;
import com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository.CustomerElasticsearchRepository;
import com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository.CustomerJpaRepository;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.request.CustomerSearchQuery;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.customers.query.repository.CustomerDetailQueryRepository;
import com.deveagles.be15_deveagles_be.features.customers.query.repository.CustomerListQueryRepository;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerQueryService;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CustomerQueryServiceImpl implements CustomerQueryService {

  private final CustomerJpaRepository customerJpaRepository;
  private final CustomerRepository customerRepository;
  private final CustomerElasticsearchRepository elasticsearchRepository;
  private final CustomerDetailQueryRepository customerDetailQueryRepository;
  private final CustomerListQueryRepository customerListQueryRepository;
  private final JPAQueryFactory queryFactory;

  // 기본 조회
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
        .toList();
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

  // 상세 조회 (조인)
  @Override
  public Optional<CustomerDetailResponse> getCustomerDetail(Long customerId, Long shopId) {
    return customerDetailQueryRepository.findCustomerDetailById(customerId, shopId);
  }

  // 목록 조회 (조인 + 페이징)
  @Override
  public List<CustomerListResponse> getCustomerList(Long shopId) {
    return customerListQueryRepository.findCustomerListByShopId(shopId);
  }

  @Override
  public Page<CustomerListResponse> getCustomerListPaged(Long shopId, Pageable pageable) {
    return customerListQueryRepository.findCustomerListByShopId(shopId, pageable);
  }

  // 검색 (Elasticsearch + JPA 폴백)
  @Override
  public List<CustomerSearchResult> searchByKeyword(String keyword, Long shopId) {
    try {
      List<CustomerDocument> documents =
          elasticsearchRepository.searchByNameOrPhoneNumber(shopId, keyword);
      return documents.stream().map(CustomerSearchResult::from).collect(Collectors.toList());
    } catch (Exception e) {
      log.warn("Elasticsearch 검색 실패, JPA로 폴백: {}", e.getMessage());
      return fallbackToJpaSearch(keyword, shopId);
    }
  }

  @Override
  public PagedResult<CustomerSearchResult> advancedSearch(CustomerSearchQuery query) {
    try {
      Sort sort =
          Sort.by(
              "DESC".equalsIgnoreCase(query.sortDirection())
                  ? Sort.Direction.DESC
                  : Sort.Direction.ASC,
              mapSortField(query.sortBy()));

      Pageable pageable = PageRequest.of(query.page(), query.size(), sort);

      // 기본 쿼리 생성
      JPAQuery<Customer> jpaQuery =
          queryFactory
              .selectFrom(customer)
              .leftJoin(customerGrade)
              .on(customer.customerGradeId.eq(customerGrade.id))
              .where(customer.shopId.eq(query.shopId()).and(customer.deletedAt.isNull()));

      // 키워드 검색
      if (query.keyword() != null && !query.keyword().trim().isEmpty()) {
        jpaQuery.where(
            customer
                .customerName
                .containsIgnoreCase(query.keyword())
                .or(customer.phoneNumber.containsIgnoreCase(query.keyword())));
      }

      // 고객 등급 필터링
      if (query.customerGradeIds() != null && !query.customerGradeIds().isEmpty()) {
        jpaQuery.where(customer.customerGradeId.in(query.customerGradeIds()));
      }

      // 태그 필터링
      if (query.tagIds() != null && !query.tagIds().isEmpty()) {
        jpaQuery
            .innerJoin(tagByCustomer)
            .on(customer.id.eq(tagByCustomer.customerId))
            .where(tagByCustomer.tagId.in(query.tagIds().stream().map(Long::valueOf).toList()));
      }

      // 성별 필터링
      if (query.gender() != null) {
        jpaQuery.where(customer.gender.eq(Customer.Gender.valueOf(query.gender())));
      }

      // 마케팅 동의 필터링
      if (query.marketingConsent() != null) {
        jpaQuery.where(customer.marketingConsent.eq(query.marketingConsent()));
      }

      // 알림 동의 필터링
      if (query.notificationConsent() != null) {
        jpaQuery.where(customer.notificationConsent.eq(query.notificationConsent()));
      }

      // 휴면 고객 제외
      if (query.excludeDormant() != null && query.excludeDormant()) {
        LocalDateTime dormantDate =
            LocalDateTime.now()
                .minusMonths(query.dormantMonths() != null ? query.dormantMonths() : 6);
        jpaQuery.where(customer.recentVisitDate.after(dormantDate.toLocalDate()));
      }

      // 최근 메시지 수신자 제외
      if (query.excludeRecentMessage() != null && query.excludeRecentMessage()) {
        LocalDateTime recentMessageDate =
            LocalDateTime.now()
                .minusDays(query.recentMessageDays() != null ? query.recentMessageDays() : 30);
        jpaQuery.where(
            customer
                .lastMessageSentAt
                .before(recentMessageDate)
                .or(customer.lastMessageSentAt.isNull()));
      }

      // 페이징 적용
      List<Customer> countResult = jpaQuery.fetch();
      long total = countResult.size();
      List<Customer> customers =
          jpaQuery.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

      // 결과 변환
      List<CustomerSearchResult> responses =
          customers.stream()
              .map(
                  c ->
                      CustomerSearchResult.of(
                          c.getId(),
                          c.getCustomerName(),
                          c.getPhoneNumber(),
                          c.getCustomerGradeId(),
                          null, // customerGradeName은 나중에 조인으로 가져오도록 수정
                          c.getGender()))
              .toList();

      Pagination pagination =
          Pagination.builder()
              .currentPage(query.page())
              .totalPages((int) Math.ceil((double) total / query.size()))
              .totalItems(total)
              .build();

      return new PagedResult<>(responses, pagination);

    } catch (Exception e) {
      log.error("JPA 고급 검색 실패: {}", e.getMessage(), e);
      throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public List<String> autocomplete(String prefix, Long shopId) {
    try {
      List<CustomerDocument> documents = elasticsearchRepository.autocomplete(shopId, prefix);
      return documents.stream()
          .map(doc -> doc.getCustomerName() + " (" + doc.getPhoneNumber() + ")")
          .distinct()
          .limit(10)
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.warn("Elasticsearch 자동완성 실패: {}", e.getMessage());
      return List.of();
    }
  }

  @Override
  public long countByKeyword(String keyword, Long shopId) {
    try {
      return elasticsearchRepository.searchByNameOrPhoneNumber(shopId, keyword).size();
    } catch (Exception e) {
      log.warn("Elasticsearch 개수 조회 실패: {}", e.getMessage());
      return 0;
    }
  }

  // 태그 조회
  @Override
  public List<TagResponse> getCustomerTags(Long customerId, Long shopId) {
    validateCustomerExists(customerId, shopId);

    return queryFactory
        .select(tag.id, tag.shopId, tag.tagName, tag.colorCode)
        .from(tagByCustomer)
        .innerJoin(tag)
        .on(tagByCustomer.tagId.eq(tag.id))
        .where(tagByCustomer.customerId.eq(customerId))
        .fetch()
        .stream()
        .map(
            tuple ->
                TagResponse.builder()
                    .tagId(tuple.get(tag.id))
                    .shopId(tuple.get(tag.shopId))
                    .tagName(tuple.get(tag.tagName))
                    .colorCode(tuple.get(tag.colorCode))
                    .build())
        .toList();
  }

  // Elasticsearch 동기화
  @Override
  @Transactional
  public void syncCustomerToElasticsearch(Long customerId) {
    try {
      customerJpaRepository
          .findById(customerId)
          .ifPresent(
              customer -> {
                CustomerDocument document = createCustomerDocumentWithGradeName(customer);
                elasticsearchRepository.save(document);
                log.info("고객 Elasticsearch 동기화 완료: ID={}", customerId);
              });
    } catch (Exception e) {
      log.error("고객 Elasticsearch 동기화 실패: ID={}, error={}", customerId, e.getMessage());
    }
  }

  @Override
  @Transactional
  public void reindexAllCustomers(Long shopId) {
    try {
      List<Customer> customers = customerJpaRepository.findByShopIdAndDeletedAtIsNull(shopId);
      List<CustomerDocument> documents =
          customers.stream()
              .map(this::createCustomerDocumentWithGradeName)
              .collect(Collectors.toList());

      elasticsearchRepository.saveAll(documents);
      log.info("매장 {} 고객 데이터 재인덱싱 완료: {}건", shopId, documents.size());
    } catch (Exception e) {
      log.error("고객 데이터 재인덱싱 실패: shopId={}, error={}", shopId, e.getMessage());
    }
  }

  @Override
  public Optional<CustomerIdResponse> findCustomerIdByPhoneNumber(String phoneNumber, Long shopId) {
    return customerJpaRepository
        .findByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId)
        .map(customer -> new CustomerIdResponse(customer.getId()));
  }

  private CustomerDocument createCustomerDocumentWithGradeName(Customer customer) {
    String gradeName =
        queryFactory
            .select(customerGrade.customerGradeName)
            .from(customerGrade)
            .where(customerGrade.id.eq(customer.getCustomerGradeId()))
            .fetchOne();

    return CustomerDocument.builder()
        .id(customer.getShopId() + "_" + customer.getId())
        .customerId(customer.getId())
        .shopId(customer.getShopId())
        .customerName(customer.getCustomerName())
        .phoneNumber(customer.getPhoneNumber())
        .customerGradeId(customer.getCustomerGradeId())
        .customerGradeName(gradeName)
        .gender(customer.getGender() != null ? customer.getGender().name() : null)
        .deletedAt(customer.getDeletedAt())
        .build();
  }

  // Private helper methods
  private void validateCustomerExists(Long customerId, Long shopId) {
    if (customerRepository.findByIdAndShopId(customerId, shopId).isEmpty()) {
      throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND, "고객을 찾을 수 없습니다.");
    }
  }

  private List<CustomerSearchResult> fallbackToJpaSearch(String keyword, Long shopId) {
    return queryFactory
        .select(
            customer.id,
            customer.customerName,
            customer.phoneNumber,
            customer.customerGradeId,
            customerGrade.customerGradeName,
            customer.gender)
        .from(customer)
        .leftJoin(customerGrade)
        .on(customer.customerGradeId.eq(customerGrade.id))
        .where(
            customer
                .shopId
                .eq(shopId)
                .and(customer.deletedAt.isNull())
                .and(
                    customer
                        .customerName
                        .contains(keyword)
                        .or(customer.phoneNumber.contains(keyword))))
        .fetch()
        .stream()
        .map(
            tuple ->
                CustomerSearchResult.of(
                    tuple.get(customer.id),
                    tuple.get(customer.customerName),
                    tuple.get(customer.phoneNumber),
                    tuple.get(customer.customerGradeId),
                    tuple.get(customerGrade.customerGradeName),
                    tuple.get(customer.gender)))
        .collect(Collectors.toList());
  }

  private String mapSortField(String sortBy) {
    return switch (sortBy) {
      case "customerName" -> "customerName.keyword";
      case "phoneNumber" -> "phoneNumber";
      default -> "customerName.keyword";
    };
  }

  @Override
  public List<String> getCustomerPhoneNumbers(List<Long> customerIds) {
    List<Customer> customers = customerJpaRepository.findAllById(customerIds);

    if (customers.size() != customerIds.size()) {
      throw new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND);
    }

    return customers.stream().map(Customer::getPhoneNumber).toList();
  }
}
