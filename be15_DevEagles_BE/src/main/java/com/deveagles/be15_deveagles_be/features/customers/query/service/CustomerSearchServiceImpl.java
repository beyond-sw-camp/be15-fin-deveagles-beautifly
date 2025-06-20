package com.deveagles.be15_deveagles_be.features.customers.query.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository.CustomerElasticsearchRepository;
import com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository.CustomerJpaRepository;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.CustomerSearchQuery;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerDocument;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerSearchResult;
import java.util.List;
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
@Slf4j
public class CustomerSearchServiceImpl implements CustomerSearchService {

  private final CustomerElasticsearchRepository elasticsearchRepository;
  private final CustomerJpaRepository jpaRepository;

  @Override
  @Transactional(readOnly = true)
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
  @Transactional(readOnly = true)
  public PagedResult<CustomerSearchResult> advancedSearch(CustomerSearchQuery query) {
    try {
      Sort sort =
          Sort.by(
              "DESC".equalsIgnoreCase(query.sortDirection())
                  ? Sort.Direction.DESC
                  : Sort.Direction.ASC,
              mapSortField(query.sortBy()));

      Pageable pageable = PageRequest.of(query.page(), query.size(), sort);

      Page<CustomerDocument> documentPage;
      if (query.keyword() != null && !query.keyword().trim().isEmpty()) {
        documentPage =
            elasticsearchRepository.advancedSearch(query.shopId(), query.keyword(), pageable);
      } else {
        documentPage =
            elasticsearchRepository.findByShopIdAndDeletedAtIsNull(query.shopId(), pageable);
      }

      List<CustomerSearchResult> responses =
          documentPage.getContent().stream()
              .map(CustomerSearchResult::from)
              .collect(Collectors.toList());

      Pagination pagination =
          Pagination.builder()
              .currentPage(documentPage.getNumber())
              .totalPages(documentPage.getTotalPages())
              .totalItems(documentPage.getTotalElements())
              .build();

      return new PagedResult<>(responses, pagination);

    } catch (Exception e) {
      log.warn("Elasticsearch 고급 검색 실패, JPA로 폴백: {}", e.getMessage());
      return fallbackToJpaAdvancedSearch(query);
    }
  }

  @Override
  @Transactional(readOnly = true)
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
  @Transactional(readOnly = true)
  public long countByKeyword(String keyword, Long shopId) {
    try {
      return elasticsearchRepository.searchByNameOrPhoneNumber(shopId, keyword).size();
    } catch (Exception e) {
      log.warn("Elasticsearch 개수 조회 실패: {}", e.getMessage());
      return 0;
    }
  }

  @Override
  @Transactional
  public void syncCustomerToElasticsearch(Long customerId) {
    try {
      jpaRepository
          .findById(customerId)
          .ifPresent(
              customer -> {
                CustomerDocument document = CustomerDocument.from(customer);
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
      List<Customer> customers = jpaRepository.findByShopIdAndDeletedAtIsNull(shopId);
      List<CustomerDocument> documents =
          customers.stream().map(CustomerDocument::from).collect(Collectors.toList());

      elasticsearchRepository.saveAll(documents);
      log.info("매장 {} 고객 데이터 재인덱싱 완료: {}건", shopId, documents.size());
    } catch (Exception e) {
      log.error("고객 데이터 재인덱싱 실패: shopId={}, error={}", shopId, e.getMessage());
    }
  }

  // JPA 폴백 메서드들 (간소화)
  private List<CustomerSearchResult> fallbackToJpaSearch(String keyword, Long shopId) {
    // JPA에서는 전체 조회 후 메모리에서 필터링
    List<Customer> customers = jpaRepository.findByShopIdAndDeletedAtIsNull(shopId);
    return customers.stream()
        .filter(
            customer ->
                customer.getCustomerName().contains(keyword)
                    || customer.getPhoneNumber().contains(keyword))
        .map(CustomerSearchResult::from)
        .collect(Collectors.toList());
  }

  private PagedResult<CustomerSearchResult> fallbackToJpaAdvancedSearch(CustomerSearchQuery query) {
    // 간단한 폴백 - 전체 조회 후 필터링
    List<Customer> customers = jpaRepository.findByShopIdAndDeletedAtIsNull(query.shopId());

    List<CustomerSearchResult> filteredResults =
        customers.stream()
            .filter(
                customer ->
                    query.keyword() == null
                        || customer.getCustomerName().contains(query.keyword())
                        || customer.getPhoneNumber().contains(query.keyword()))
            .map(CustomerSearchResult::from)
            .collect(Collectors.toList());

    // 간단한 페이징
    int start = query.page() * query.size();
    int end = Math.min(start + query.size(), filteredResults.size());
    List<CustomerSearchResult> pagedResults = filteredResults.subList(start, end);

    Pagination pagination =
        Pagination.builder()
            .currentPage(query.page())
            .totalPages((filteredResults.size() + query.size() - 1) / query.size())
            .totalItems(filteredResults.size())
            .build();

    return new PagedResult<>(pagedResults, pagination);
  }

  private String mapSortField(String sortBy) {
    return switch (sortBy) {
      case "customerName" -> "customerName.keyword";
      case "phoneNumber" -> "phoneNumber";
      default -> "customerName.keyword";
    };
  }
}
