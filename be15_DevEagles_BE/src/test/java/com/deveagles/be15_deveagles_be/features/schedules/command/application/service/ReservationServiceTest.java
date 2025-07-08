package com.deveagles.be15_deveagles_be.features.schedules.command.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.events.ReservationCreatedEvent;
import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerIdResponse;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerQueryService;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.CreateReservationFullRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.CreateReservationRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.UpdateReservationRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.Reservation;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationDetail;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationStatusName;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.ReservationDetailRepository;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

class ReservationServiceTest {

  @Mock private ReservationRepository reservationRepository;

  @Mock private ReservationDetailRepository reservationDetailRepository;

  @Mock private CustomerQueryService customerQueryService;

  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private ReservationService reservationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private CreateReservationRequest buildRequest() {
    return new CreateReservationRequest(
        1L, // shopId
        2L, // staffId
        null, // customerId
        "김하늘", // customerName
        "01012345678", // customerPhone
        "고객 요청 메모", // reservationMemo
        LocalDateTime.now().plusDays(1),
        LocalDateTime.now().plusDays(1).plusHours(1),
        List.of(101L, 102L) // secondaryItemIds
        );
  }

  @Test
  @DisplayName("기존 고객이 존재할 경우 - 고객 ID 사용")
  void createReservationWithExistingCustomer() {
    // given
    CreateReservationRequest request = buildRequest();

    // customerQueryService가 반환할 DTO
    CustomerIdResponse customerIdResponse = new CustomerIdResponse(99L);

    when(customerQueryService.findCustomerIdByPhoneNumber("01012345678", 1L))
        .thenReturn(Optional.of(customerIdResponse));

    when(reservationRepository.save(any(Reservation.class)))
        .thenAnswer(
            invocation -> {
              Reservation r = invocation.getArgument(0);
              ReflectionTestUtils.setField(r, "reservationId", 123L);
              return r;
            });

    // when
    Long resultId = reservationService.createReservation(request);

    // then
    assertThat(resultId).isEqualTo(123L);
    verify(reservationRepository).save(any(Reservation.class));
    verify(reservationDetailRepository, times(2)).save(any(ReservationDetail.class));
    verify(eventPublisher).publishEvent(any(ReservationCreatedEvent.class));
  }

  @Test
  @DisplayName("고객이 없을 경우 - 임시 고객 메모 생성")
  void createReservationWithNewCustomer() {
    // given
    CreateReservationRequest request = buildRequest();

    when(customerQueryService.findCustomerIdByPhoneNumber("01012345678", 1L))
        .thenReturn(Optional.empty());

    when(reservationRepository.save(any(Reservation.class)))
        .thenAnswer(
            invocation -> {
              Reservation r = invocation.getArgument(0);
              ReflectionTestUtils.setField(r, "reservationId", 456L);
              return r;
            });

    // when
    Long resultId = reservationService.createReservation(request);

    // then
    assertThat(resultId).isEqualTo(456L);
    verify(reservationRepository).save(any(Reservation.class));
    verify(reservationDetailRepository, times(2)).save(any(ReservationDetail.class));
    verify(eventPublisher).publishEvent(any(ReservationCreatedEvent.class));
  }

  @Test
  @DisplayName("고객 ID가 있는 경우 - createFullReservation 성공")
  void createFullReservation_success() {
    // given
    CreateReservationFullRequest request =
        new CreateReservationFullRequest(
            1L,
            2L,
            99L,
            "담당자 메모",
            "고객 메모",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1).plusHours(1),
            List.of(101L, 102L));

    when(reservationRepository.save(any(Reservation.class)))
        .thenAnswer(
            invocation -> {
              Reservation r = invocation.getArgument(0);
              ReflectionTestUtils.setField(r, "reservationId", 789L);
              return r;
            });

    // when
    Long resultId = reservationService.createFullReservation(1L, request);

    // then
    assertThat(resultId).isEqualTo(789L);
    verify(reservationRepository).save(any(Reservation.class));
    verify(reservationDetailRepository, times(2)).save(any(ReservationDetail.class));
  }

  @Test
  @DisplayName("예약 수정 성공 - 매장 일치")
  void updateReservation_success() {
    // given
    Long reservationId = 1L;
    Long shopId = 10L;

    Reservation reservation =
        Reservation.builder()
            .reservationId(reservationId)
            .shopId(shopId)
            .staffId(2L)
            .reservationStatusName(ReservationStatusName.PENDING)
            .reservationStartAt(LocalDateTime.now())
            .reservationEndAt(LocalDateTime.now().plusHours(1))
            .build();

    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

    UpdateReservationRequest request =
        new UpdateReservationRequest(
            3L,
            "CONFIRMED",
            "메모 수정",
            "고객 메모 수정",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1).plusHours(1),
            List.of(201L, 202L));

    // when
    reservationService.updateReservation(shopId, reservationId, request);

    // then
    verify(reservationDetailRepository).deleteByReservationId(reservationId);
    verify(reservationDetailRepository).saveAll(anyList());
  }

  @Test
  @DisplayName("예약 수정 실패 - 매장 불일치")
  void updateReservation_shopMismatch() {
    // given
    Long reservationId = 1L;

    Reservation reservation =
        Reservation.builder()
            .reservationId(reservationId)
            .shopId(999L) // 실제 매장 ID
            .build();

    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

    UpdateReservationRequest request =
        new UpdateReservationRequest(
            3L, "PENDING", "memo", "memo", LocalDateTime.now(), LocalDateTime.now(), List.of());

    // then
    assertThatThrownBy(() -> reservationService.updateReservation(1L, reservationId, request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("예약을 찾을 수 없습니다"); // ErrorCode.RESERVATION_NOT_FOUND 메시지 기준
  }

  @Test
  @DisplayName("예약 상태 변경 성공")
  void changeReservationStatus_success() {
    // given
    Long reservationId = 1L;
    Reservation reservation =
        Reservation.builder()
            .reservationId(reservationId)
            .shopId(1L)
            .reservationStatusName(ReservationStatusName.PENDING)
            .build();

    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

    // when
    reservationService.changeReservationStatus(1L, reservationId, ReservationStatusName.CONFIRMED);

    // then
    assertThat(reservation.getReservationStatusName()).isEqualTo(ReservationStatusName.CONFIRMED);
  }

  @Test
  @DisplayName("예약 상태 변경 실패 - 이미 결제 완료")
  void changeReservationStatus_paid() {
    // given
    Long reservationId = 1L;
    Reservation reservation =
        Reservation.builder()
            .reservationId(reservationId)
            .shopId(1L)
            .reservationStatusName(ReservationStatusName.PAID)
            .build();

    when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

    // then
    assertThatThrownBy(
            () ->
                reservationService.changeReservationStatus(
                    1L, reservationId, ReservationStatusName.CONFIRMED))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("PAID 상태의 예약은 수정할 수 없습니다"); // MODIFY_NOT_ALLOWED_FOR_PAID_RESERVATION
  }
}
