package com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Customer 도메인 테스트")
class CustomerTest {

  private Customer customer;

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
  }

  @Test
  @DisplayName("고객 정보 수정 - 성공")
  void updateCustomerInfo_Success() {
    // Given
    String newName = "김수정";
    String newPhone = "01087654321";
    String newMemo = "수정된 메모";
    Customer.Gender newGender = Customer.Gender.F;
    Long newChannelId = 2L;

    // When
    customer.updateCustomerInfo(newName, newPhone, newMemo, newGender, newChannelId);

    // Then
    assertThat(customer.getCustomerName()).isEqualTo(newName);
    assertThat(customer.getPhoneNumber()).isEqualTo(newPhone);
    assertThat(customer.getMemo()).isEqualTo(newMemo);
    assertThat(customer.getGender()).isEqualTo(newGender);
    assertThat(customer.getChannelId()).isEqualTo(newChannelId);
  }

  @Test
  @DisplayName("마케팅 동의 업데이트 - 동의 시 시간 기록")
  void updateMarketingConsent_ConsentTrue_RecordsTime() {
    // Given
    Boolean consent = true;

    // When
    customer.updateMarketingConsent(consent);

    // Then
    assertThat(customer.getMarketingConsent()).isTrue();
    assertThat(customer.getMarketingConsentedAt()).isNotNull();
    assertThat(customer.getMarketingConsentedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
  }

  @Test
  @DisplayName("마케팅 동의 업데이트 - 거부 시 시간 null")
  void updateMarketingConsent_ConsentFalse_ClearsTime() {
    // Given
    customer.updateMarketingConsent(true); // 먼저 동의
    Boolean consent = false;

    // When
    customer.updateMarketingConsent(consent);

    // Then
    assertThat(customer.getMarketingConsent()).isFalse();
    assertThat(customer.getMarketingConsentedAt()).isNull();
  }

  @Test
  @DisplayName("알림 동의 업데이트 - 성공")
  void updateNotificationConsent_Success() {
    // Given
    Boolean consent = false;

    // When
    customer.updateNotificationConsent(consent);

    // Then
    assertThat(customer.getNotificationConsent()).isFalse();
  }

  @Test
  @DisplayName("방문 추가 - 방문 횟수와 매출 증가, 최근 방문일 업데이트")
  void addVisit_Success() {
    // Given
    Integer revenue = 50000;
    Integer initialVisitCount = customer.getVisitCount();
    Integer initialTotalRevenue = customer.getTotalRevenue();

    // When
    customer.addVisit(revenue);

    // Then
    assertThat(customer.getVisitCount()).isEqualTo(initialVisitCount + 1);
    assertThat(customer.getTotalRevenue()).isEqualTo(initialTotalRevenue + revenue);
    assertThat(customer.getRecentVisitDate()).isEqualTo(LocalDate.now());
  }

  @Test
  @DisplayName("노쇼 추가 - 노쇼 횟수 증가")
  void addNoshow_Success() {
    // Given
    Integer initialNoshowCount = customer.getNoshowCount();

    // When
    customer.addNoshow();

    // Then
    assertThat(customer.getNoshowCount()).isEqualTo(initialNoshowCount + 1);
  }

  @Test
  @DisplayName("메시지 발송 시간 업데이트 - 성공")
  void updateLastMessageSentAt_Success() {
    // When
    customer.updateLastMessageSentAt();

    // Then
    assertThat(customer.getLastMessageSentAt()).isNotNull();
    assertThat(customer.getLastMessageSentAt()).isBefore(LocalDateTime.now().plusSeconds(1));
  }

  @Test
  @DisplayName("소프트 삭제 - 삭제 시간 기록")
  void softDelete_Success() {
    // When
    customer.softDelete();

    // Then
    assertThat(customer.getDeletedAt()).isNotNull();
    assertThat(customer.isDeleted()).isTrue();
    assertThat(customer.getDeletedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
  }

  @Test
  @DisplayName("삭제 상태 확인 - 삭제되지 않은 고객")
  void isDeleted_NotDeleted() {
    // When & Then
    assertThat(customer.isDeleted()).isFalse();
  }
}
