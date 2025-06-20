package com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
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
@DisplayName("CustomerRepository 테스트")
class CustomerRepositoryImplTest {

  @Mock private CustomerJpaRepository jpaRepository;

  @InjectMocks private CustomerRepositoryImpl customerRepository;

  private Customer customer;
  private Long shopId = 1L;

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
  }

  @Test
  @DisplayName("고객 저장 - 성공")
  void save_Success() {
    // Given
    given(jpaRepository.save(customer)).willReturn(customer);

    // When
    Customer result = customerRepository.save(customer);

    // Then
    assertThat(result).isEqualTo(customer);
    verify(jpaRepository).save(customer);
  }

  @Test
  @DisplayName("ID로 고객 조회 - 성공")
  void findById_Success() {
    // Given
    Long customerId = 1L;
    given(jpaRepository.findById(customerId)).willReturn(Optional.of(customer));

    // When
    Optional<Customer> result = customerRepository.findById(customerId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(customer);
    verify(jpaRepository).findById(customerId);
  }

  @Test
  @DisplayName("ID와 매장ID로 고객 조회 - 성공")
  void findByIdAndShopId_Success() {
    // Given
    Long customerId = 1L;
    given(jpaRepository.findByIdAndShopIdAndDeletedAtIsNull(customerId, shopId))
        .willReturn(Optional.of(customer));

    // When
    Optional<Customer> result = customerRepository.findByIdAndShopId(customerId, shopId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(customer);
    verify(jpaRepository).findByIdAndShopIdAndDeletedAtIsNull(customerId, shopId);
  }

  @Test
  @DisplayName("매장ID로 고객 목록 조회 - 성공")
  void findByShopIdAndDeletedAtIsNull_Success() {
    // Given
    List<Customer> customers = Arrays.asList(customer);
    given(jpaRepository.findByShopIdAndDeletedAtIsNull(shopId)).willReturn(customers);

    // When
    List<Customer> result = customerRepository.findByShopIdAndDeletedAtIsNull(shopId);

    // Then
    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(customer);
    verify(jpaRepository).findByShopIdAndDeletedAtIsNull(shopId);
  }

  @Test
  @DisplayName("전화번호와 매장ID로 고객 조회 - 성공")
  void findByPhoneNumberAndShopId_Success() {
    // Given
    String phoneNumber = "01012345678";
    given(jpaRepository.findByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId))
        .willReturn(Optional.of(customer));

    // When
    Optional<Customer> result = customerRepository.findByPhoneNumberAndShopId(phoneNumber, shopId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(customer);
    verify(jpaRepository).findByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId);
  }

  @Test
  @DisplayName("고객 삭제 - 성공")
  void delete_Success() {
    // Given
    willDoNothing().given(jpaRepository).delete(customer);

    // When
    customerRepository.delete(customer);

    // Then
    verify(jpaRepository).delete(customer);
  }

  @Test
  @DisplayName("전화번호 중복 확인 - 존재함")
  void existsByPhoneNumberAndShopId_Exists() {
    // Given
    String phoneNumber = "01012345678";
    given(jpaRepository.existsByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId))
        .willReturn(true);

    // When
    boolean result = customerRepository.existsByPhoneNumberAndShopId(phoneNumber, shopId);

    // Then
    assertThat(result).isTrue();
    verify(jpaRepository).existsByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId);
  }

  @Test
  @DisplayName("전화번호 중복 확인 - 존재하지 않음")
  void existsByPhoneNumberAndShopId_NotExists() {
    // Given
    String phoneNumber = "01099999999";
    given(jpaRepository.existsByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId))
        .willReturn(false);

    // When
    boolean result = customerRepository.existsByPhoneNumberAndShopId(phoneNumber, shopId);

    // Then
    assertThat(result).isFalse();
    verify(jpaRepository).existsByPhoneNumberAndShopIdAndDeletedAtIsNull(phoneNumber, shopId);
  }
}
