package com.deveagles.be15_deveagles_be.features.customers.command.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateCustomerRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateCustomerRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.CustomerResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.CustomerRepository;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerSearchService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerCommandService 테스트")
class CustomerCommandServiceImplTest {

  @Mock private CustomerRepository customerRepository;

  @Mock private CustomerSearchService customerSearchService;

  @InjectMocks private CustomerCommandServiceImpl customerCommandService;

  private Customer customer;
  private CreateCustomerRequest createRequest;
  private UpdateCustomerRequest updateRequest;

  @BeforeEach
  void setUp() {
    customer =
        Customer.builder()
            .customerGradeId(1L)
            .shopId(1L)
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

    createRequest =
        new CreateCustomerRequest(
            1L,
            1L,
            1L,
            "김고객",
            "01012345678",
            "VIP 고객",
            LocalDate.of(1990, 1, 1),
            LocalDateTime.now(),
            Customer.Gender.M,
            true,
            true,
            1L);

    updateRequest =
        new UpdateCustomerRequest(1L, "김수정", "01087654321", "수정된 메모", Customer.Gender.F, 2L);
  }

  @Test
  @DisplayName("고객 생성 - 성공")
  void createCustomer_Success() {
    // Given
    given(customerRepository.existsByPhoneNumberAndShopId(anyString(), anyLong()))
        .willReturn(false);
    given(customerRepository.save(any(Customer.class))).willReturn(customer);
    willDoNothing().given(customerSearchService).syncCustomerToElasticsearch(anyLong());

    // When
    CustomerResponse result = customerCommandService.createCustomer(createRequest);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.customerName()).isEqualTo(createRequest.customerName());
    assertThat(result.phoneNumber()).isEqualTo(createRequest.phoneNumber());

    verify(customerRepository)
        .existsByPhoneNumberAndShopId(createRequest.phoneNumber(), createRequest.shopId());
    verify(customerRepository).save(any(Customer.class));
    verify(customerSearchService).syncCustomerToElasticsearch(anyLong());
  }

  @Test
  @DisplayName("고객 생성 - 중복 전화번호로 실패")
  void createCustomer_DuplicatePhoneNumber_ThrowsException() {
    // Given
    given(customerRepository.existsByPhoneNumberAndShopId(anyString(), anyLong())).willReturn(true);

    // When & Then
    assertThatThrownBy(() -> customerCommandService.createCustomer(createRequest))
        .isInstanceOf(BusinessException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CUSTOMER_PHONE_DUPLICATE);

    verify(customerRepository, never()).save(any(Customer.class));
    verify(customerSearchService, never()).syncCustomerToElasticsearch(anyLong());
  }

  @Test
  @DisplayName("고객 수정 - 성공")
  void updateCustomer_Success() {
    // Given
    given(customerRepository.findByIdAndShopId(anyLong(), anyLong()))
        .willReturn(Optional.of(customer));
    given(customerRepository.save(any(Customer.class))).willReturn(customer);
    willDoNothing().given(customerSearchService).syncCustomerToElasticsearch(anyLong());

    // When
    CustomerResponse result = customerCommandService.updateCustomer(updateRequest);

    // Then
    assertThat(result).isNotNull();
    verify(customerRepository).findByIdAndShopId(updateRequest.customerId(), 1L);
    verify(customerRepository).save(customer);
    verify(customerSearchService).syncCustomerToElasticsearch(customer.getId());
  }

  @Test
  @DisplayName("고객 수정 - 고객을 찾을 수 없어서 실패")
  void updateCustomer_CustomerNotFound_ThrowsException() {
    // Given
    given(customerRepository.findByIdAndShopId(anyLong(), anyLong())).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> customerCommandService.updateCustomer(updateRequest))
        .isInstanceOf(BusinessException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CUSTOMER_NOT_FOUND);

    verify(customerRepository, never()).save(any(Customer.class));
    verify(customerSearchService, never()).syncCustomerToElasticsearch(anyLong());
  }

  @Test
  @DisplayName("고객 삭제 - 성공")
  void deleteCustomer_Success() {
    // Given
    Long customerId = 1L;
    Long shopId = 1L;
    given(customerRepository.findByIdAndShopId(customerId, shopId))
        .willReturn(Optional.of(customer));
    given(customerRepository.save(any(Customer.class))).willReturn(customer);

    // When
    customerCommandService.deleteCustomer(customerId, shopId);

    // Then
    verify(customerRepository).findByIdAndShopId(customerId, shopId);
    verify(customerRepository).save(customer);
  }

  @Test
  @DisplayName("마케팅 동의 업데이트 - 성공")
  void updateMarketingConsent_Success() {
    // Given
    Long customerId = 1L;
    Long shopId = 1L;
    Boolean consent = false;

    given(customerRepository.findByIdAndShopId(customerId, shopId))
        .willReturn(Optional.of(customer));
    given(customerRepository.save(any(Customer.class))).willReturn(customer);

    // When
    CustomerResponse result =
        customerCommandService.updateMarketingConsent(customerId, shopId, consent);

    // Then
    assertThat(result).isNotNull();
    verify(customerRepository).findByIdAndShopId(customerId, shopId);
    verify(customerRepository).save(customer);
  }

  @Test
  @DisplayName("알림 동의 업데이트 - 성공")
  void updateNotificationConsent_Success() {
    // Given
    Long customerId = 1L;
    Long shopId = 1L;
    Boolean consent = false;

    given(customerRepository.findByIdAndShopId(customerId, shopId))
        .willReturn(Optional.of(customer));
    given(customerRepository.save(any(Customer.class))).willReturn(customer);

    // When
    CustomerResponse result =
        customerCommandService.updateNotificationConsent(customerId, shopId, consent);

    // Then
    assertThat(result).isNotNull();
    verify(customerRepository).findByIdAndShopId(customerId, shopId);
    verify(customerRepository).save(customer);
  }

  @Test
  @DisplayName("방문 추가 - 성공")
  void addVisit_Success() {
    // Given
    Long customerId = 1L;
    Long shopId = 1L;
    Integer revenue = 50000;

    given(customerRepository.findByIdAndShopId(customerId, shopId))
        .willReturn(Optional.of(customer));
    given(customerRepository.save(any(Customer.class))).willReturn(customer);

    // When
    CustomerResponse result = customerCommandService.addVisit(customerId, shopId, revenue);

    // Then
    assertThat(result).isNotNull();
    verify(customerRepository).findByIdAndShopId(customerId, shopId);
    verify(customerRepository).save(customer);
  }

  @Test
  @DisplayName("노쇼 추가 - 성공")
  void addNoshow_Success() {
    // Given
    Long customerId = 1L;
    Long shopId = 1L;

    given(customerRepository.findByIdAndShopId(customerId, shopId))
        .willReturn(Optional.of(customer));
    given(customerRepository.save(any(Customer.class))).willReturn(customer);

    // When
    CustomerResponse result = customerCommandService.addNoshow(customerId, shopId);

    // Then
    assertThat(result).isNotNull();
    verify(customerRepository).findByIdAndShopId(customerId, shopId);
    verify(customerRepository).save(customer);
  }
}
