package com.deveagles.be15_deveagles_be.features.customers.query.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.CustomerResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository.CustomerJpaRepository;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerQueryService 테스트")
class CustomerQueryServiceImplTest {

  @Mock private CustomerJpaRepository customerJpaRepository;

  @InjectMocks private CustomerQueryServiceImpl customerQueryService;

  private Customer customer1;
  private Customer customer2;
  private Long shopId = 1L;

  @BeforeEach
  void setUp() {
    customer1 =
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

    customer2 =
        Customer.builder()
            .customerGradeId(2L)
            .shopId(shopId)
            .staffId(2L)
            .customerName("이고객")
            .phoneNumber("01087654321")
            .memo("일반 고객")
            .birthdate(LocalDate.of(1985, 5, 15))
            .registeredAt(LocalDateTime.now())
            .gender(Customer.Gender.F)
            .marketingConsent(false)
            .notificationConsent(true)
            .channelId(1L)
            .build();
  }

  @Test
  @DisplayName("ID로 고객 조회 - 성공")
  void getCustomerById_Success() {
    // Given
    Long customerId = 1L;
    given(customerJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(customerId, shopId))
        .willReturn(Optional.of(customer1));

    // When
    Optional<CustomerResponse> result = customerQueryService.getCustomerById(customerId, shopId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().customerName()).isEqualTo(customer1.getCustomerName());
    assertThat(result.get().phoneNumber()).isEqualTo(customer1.getPhoneNumber());

    verify(customerJpaRepository).findByIdAndShopIdAndDeletedAtIsNull(customerId, shopId);
  }

  @Test
  @DisplayName("ID로 고객 조회 - 고객을 찾을 수 없음")
  void getCustomerById_NotFound() {
    // Given
    Long customerId = 999L;
    given(customerJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(customerId, shopId))
        .willReturn(Optional.empty());

    // When
    Optional<CustomerResponse> result = customerQueryService.getCustomerById(customerId, shopId);

    // Then
    assertThat(result).isEmpty();
    verify(customerJpaRepository).findByIdAndShopIdAndDeletedAtIsNull(customerId, shopId);
  }

  @Test
  @DisplayName("전화번호로 고객 조회 - 성공")
  void getCustomerByPhoneNumber_Success() {
    // Given
    String phoneNumber = "01012345678";
    given(customerJpaRepository.findByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId))
        .willReturn(Optional.of(customer1));

    // When
    Optional<CustomerResponse> result =
        customerQueryService.getCustomerByPhoneNumber(phoneNumber, shopId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().phoneNumber()).isEqualTo(phoneNumber);

    verify(customerJpaRepository).findByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId);
  }

  @Test
  @DisplayName("매장별 고객 목록 조회 - 성공")
  void getCustomersByShopId_Success() {
    // Given
    List<Customer> customers = Arrays.asList(customer1, customer2);
    given(customerJpaRepository.findByShopIdAndDeletedAtIsNull(shopId)).willReturn(customers);

    // When
    List<CustomerResponse> result = customerQueryService.getCustomersByShopId(shopId);

    // Then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).customerName()).isEqualTo(customer1.getCustomerName());
    assertThat(result.get(1).customerName()).isEqualTo(customer2.getCustomerName());

    verify(customerJpaRepository).findByShopIdAndDeletedAtIsNull(shopId);
  }

  @Test
  @DisplayName("매장별 고객 수 조회 - 성공")
  void getCustomerCountByShopId_Success() {
    // Given
    long expectedCount = 10L;
    given(customerJpaRepository.countByShopIdAndDeletedAtIsNull(shopId)).willReturn(expectedCount);

    // When
    long result = customerQueryService.getCustomerCountByShopId(shopId);

    // Then
    assertThat(result).isEqualTo(expectedCount);
    verify(customerJpaRepository).countByShopIdAndDeletedAtIsNull(shopId);
  }

  @Test
  @DisplayName("전화번호 중복 확인 - 존재함")
  void existsByPhoneNumber_Exists() {
    // Given
    String phoneNumber = "01012345678";
    given(customerJpaRepository.existsByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId))
        .willReturn(true);

    // When
    boolean result = customerQueryService.existsByPhoneNumber(phoneNumber, shopId);

    // Then
    assertThat(result).isTrue();
    verify(customerJpaRepository)
        .existsByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId);
  }

  @Test
  @DisplayName("전화번호 중복 확인 - 존재하지 않음")
  void existsByPhoneNumber_NotExists() {
    // Given
    String phoneNumber = "01099999999";
    given(customerJpaRepository.existsByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId))
        .willReturn(false);

    // When
    boolean result = customerQueryService.existsByPhoneNumber(phoneNumber, shopId);

    // Then
    assertThat(result).isFalse();
    verify(customerJpaRepository)
        .existsByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId);
  }
}
