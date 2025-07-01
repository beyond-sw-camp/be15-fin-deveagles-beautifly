package com.deveagles.be15_deveagles_be.features.schedules.command.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerIdResponse;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerQueryService;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.CreateReservationRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.Reservation;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationDetail;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.ReservationDetailRepository;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

class ReservationServiceTest {

  @Mock private ReservationRepository reservationRepository;

  @Mock private ReservationDetailRepository reservationDetailRepository;

  @Mock private CustomerQueryService customerQueryService;

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
  }
}
