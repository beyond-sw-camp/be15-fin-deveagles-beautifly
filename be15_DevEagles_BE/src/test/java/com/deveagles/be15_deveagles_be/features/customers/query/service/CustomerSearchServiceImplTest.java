package com.deveagles.be15_deveagles_be.features.customers.query.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository.CustomerElasticsearchRepository;
import com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository.CustomerJpaRepository;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.CustomerSearchQuery;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerDocument;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerSearchResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerSearchService 테스트")
class CustomerSearchServiceImplTest {

  @Mock private CustomerElasticsearchRepository elasticsearchRepository;

  @Mock private CustomerJpaRepository jpaRepository;

  @InjectMocks private CustomerSearchServiceImpl customerSearchService;

  private Customer customer;
  private CustomerDocument customerDocument;
  private Long shopId = 1L;
  private String keyword = "김고객";

  @BeforeEach
  void setUp() {
    customer =
        Customer.builder()
            .customerGradeId(1L)
            .shopId(shopId)
            .staffId(1L)
            .customerName("김고객")
            .phoneNumber("01012345678")
            .memo("VIP 고객")
            .birthdate(LocalDate.of(1990, 1, 1))
            .registeredAt(LocalDateTime.now())
            .gender(Customer.Gender.M)
            .marketingConsent(true)
            .notificationConsent(true)
            .channelId(1L)
            .build();

    customerDocument =
        CustomerDocument.builder()
            .customerId(1L)
            .shopId(shopId)
            .customerName("김고객")
            .phoneNumber("01012345678")
            .customerGradeId(1L)
            .gender("M")
            .build();
  }

  @Test
  @DisplayName("키워드 검색 - Elasticsearch 성공")
  void searchByKeyword_ElasticsearchSuccess() {
    // Given
    List<CustomerDocument> documents = Arrays.asList(customerDocument);
    given(elasticsearchRepository.searchByNameOrPhoneNumber(shopId, keyword)).willReturn(documents);

    // When
    List<CustomerSearchResult> result = customerSearchService.searchByKeyword(keyword, shopId);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).customerName()).isEqualTo(customerDocument.getCustomerName());

    verify(elasticsearchRepository).searchByNameOrPhoneNumber(shopId, keyword);
    verify(jpaRepository, never()).findByShopIdAndDeletedAtIsNull(anyLong());
  }

  @Test
  @DisplayName("키워드 검색 - Elasticsearch 실패 시 JPA 폴백")
  void searchByKeyword_ElasticsearchFailure_FallbackToJpa() {
    // Given
    given(elasticsearchRepository.searchByNameOrPhoneNumber(shopId, keyword))
        .willThrow(new RuntimeException("Elasticsearch 연결 실패"));
    given(jpaRepository.findByShopIdAndDeletedAtIsNull(shopId)).willReturn(Arrays.asList(customer));

    // When
    List<CustomerSearchResult> result = customerSearchService.searchByKeyword(keyword, shopId);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).customerName()).isEqualTo(customer.getCustomerName());

    verify(elasticsearchRepository).searchByNameOrPhoneNumber(shopId, keyword);
    verify(jpaRepository).findByShopIdAndDeletedAtIsNull(shopId);
  }

  @Test
  @DisplayName("고급 검색 - 키워드 있음")
  void advancedSearch_WithKeyword() {
    // Given
    CustomerSearchQuery query =
        new CustomerSearchQuery(
            shopId, keyword, null, null, null, null, false, 0, 10, "customerName", "ASC");

    Pageable pageable = PageRequest.of(0, 10);
    Page<CustomerDocument> documentPage =
        new PageImpl<>(Arrays.asList(customerDocument), pageable, 1);

    given(elasticsearchRepository.advancedSearch(eq(shopId), eq(keyword), any(Pageable.class)))
        .willReturn(documentPage);

    // When
    PagedResult<CustomerSearchResult> result = customerSearchService.advancedSearch(query);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getPagination().getTotalItems()).isEqualTo(1);
    assertThat(result.getContent().get(0).customerName())
        .isEqualTo(customerDocument.getCustomerName());

    verify(elasticsearchRepository).advancedSearch(eq(shopId), eq(keyword), any(Pageable.class));
  }

  @Test
  @DisplayName("고급 검색 - 키워드 없음")
  void advancedSearch_WithoutKeyword() {
    // Given
    CustomerSearchQuery query =
        new CustomerSearchQuery(
            shopId, null, null, null, null, null, false, 0, 10, "customerName", "ASC");

    Pageable pageable = PageRequest.of(0, 10);
    Page<CustomerDocument> documentPage =
        new PageImpl<>(Arrays.asList(customerDocument), pageable, 1);

    given(elasticsearchRepository.findByShopIdAndDeletedAtIsNull(eq(shopId), any(Pageable.class)))
        .willReturn(documentPage);

    // When
    PagedResult<CustomerSearchResult> result = customerSearchService.advancedSearch(query);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getPagination().getTotalItems()).isEqualTo(1);

    verify(elasticsearchRepository).findByShopIdAndDeletedAtIsNull(eq(shopId), any(Pageable.class));
    verify(elasticsearchRepository, never())
        .advancedSearch(anyLong(), anyString(), any(Pageable.class));
  }

  @Test
  @DisplayName("자동완성 - 성공")
  void autocomplete_Success() {
    // Given
    String prefix = "김";
    List<CustomerDocument> documents = Arrays.asList(customerDocument);
    given(elasticsearchRepository.autocomplete(shopId, prefix)).willReturn(documents);

    // When
    List<String> result = customerSearchService.autocomplete(prefix, shopId);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).contains(customerDocument.getCustomerName());
    assertThat(result.get(0)).contains(customerDocument.getPhoneNumber());

    verify(elasticsearchRepository).autocomplete(shopId, prefix);
  }

  @Test
  @DisplayName("자동완성 - Elasticsearch 실패")
  void autocomplete_ElasticsearchFailure() {
    // Given
    String prefix = "김";
    given(elasticsearchRepository.autocomplete(shopId, prefix))
        .willThrow(new RuntimeException("Elasticsearch 연결 실패"));

    // When
    List<String> result = customerSearchService.autocomplete(prefix, shopId);

    // Then
    assertThat(result).isEmpty();

    verify(elasticsearchRepository).autocomplete(shopId, prefix);
  }

  @Test
  @DisplayName("키워드별 개수 조회 - 성공")
  void countByKeyword_Success() {
    // Given
    List<CustomerDocument> documents = Arrays.asList(customerDocument);
    given(elasticsearchRepository.searchByNameOrPhoneNumber(shopId, keyword)).willReturn(documents);

    // When
    long result = customerSearchService.countByKeyword(keyword, shopId);

    // Then
    assertThat(result).isEqualTo(1);

    verify(elasticsearchRepository).searchByNameOrPhoneNumber(shopId, keyword);
  }

  @Test
  @DisplayName("Elasticsearch 동기화 - 성공")
  void syncCustomerToElasticsearch_Success() {
    // Given
    Long customerId = 1L;
    given(jpaRepository.findById(customerId)).willReturn(Optional.of(customer));
    given(elasticsearchRepository.save(any(CustomerDocument.class))).willReturn(customerDocument);

    // When
    customerSearchService.syncCustomerToElasticsearch(customerId);

    // Then
    verify(jpaRepository).findById(customerId);
    verify(elasticsearchRepository).save(any(CustomerDocument.class));
  }

  @Test
  @DisplayName("Elasticsearch 동기화 - 고객을 찾을 수 없음")
  void syncCustomerToElasticsearch_CustomerNotFound() {
    // Given
    Long customerId = 999L;
    given(jpaRepository.findById(customerId)).willReturn(Optional.empty());

    // When
    customerSearchService.syncCustomerToElasticsearch(customerId);

    // Then
    verify(jpaRepository).findById(customerId);
    verify(elasticsearchRepository, never()).save(any(CustomerDocument.class));
  }

  @Test
  @DisplayName("전체 재인덱싱 - 성공")
  void reindexAllCustomers_Success() {
    // Given
    List<Customer> customers = Arrays.asList(customer);
    given(jpaRepository.findByShopIdAndDeletedAtIsNull(shopId)).willReturn(customers);
    given(elasticsearchRepository.saveAll(anyList())).willReturn(Arrays.asList(customerDocument));

    // When
    customerSearchService.reindexAllCustomers(shopId);

    // Then
    verify(jpaRepository).findByShopIdAndDeletedAtIsNull(shopId);
    verify(elasticsearchRepository).saveAll(anyList());
  }
}
