package com.deveagles.be15_deveagles_be.features.schedules.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.UpdateReservationSettingRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationSetting;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationSettingId;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.ReservationSettingRepository;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("예약 설정 수정 테스트")
class ReservationSettingCommandServiceTest {

  @InjectMocks private ReservationSettingCommandService reservationSettingCommandService;

  @Mock private ReservationSettingRepository reservationSettingRepository;

  @Test
  void 기존_요일이_요청에_없으면_softDelete_처리된다() {
    Long shopId = 1L;

    ReservationSetting oldSetting =
        ReservationSetting.builder()
            .id(new ReservationSettingId(shopId, 0))
            .availableStartTime(LocalTime.of(9, 0))
            .availableEndTime(LocalTime.of(18, 0))
            .build();

    when(reservationSettingRepository.findAllSettingsIncludingDeleted(shopId))
        .thenReturn(List.of(oldSetting));
    when(reservationSettingRepository.findById(any())).thenReturn(Optional.empty());

    UpdateReservationSettingRequest req =
        new UpdateReservationSettingRequest(
            1, LocalTime.of(10, 0), LocalTime.of(17, 0), null, null);

    reservationSettingCommandService.updateReservationSettings(shopId, List.of(req));

    assertThat(oldSetting.getDeletedAt()).isNotNull();
  }

  @Test
  void 기존_설정은_업데이트되고_deletedAt_복구된다() {
    Long shopId = 1L;
    ReservationSettingId id = new ReservationSettingId(shopId, 1);
    ReservationSetting setting =
        ReservationSetting.builder()
            .id(id)
            .availableStartTime(LocalTime.of(9, 0))
            .availableEndTime(LocalTime.of(18, 0))
            .build();
    setting.markDeleted();

    when(reservationSettingRepository.findAllSettingsIncludingDeleted(shopId))
        .thenReturn(List.of(setting));
    when(reservationSettingRepository.findById(id)).thenReturn(Optional.of(setting));

    UpdateReservationSettingRequest req =
        new UpdateReservationSettingRequest(
            1, LocalTime.of(10, 0), LocalTime.of(17, 0), null, null);

    reservationSettingCommandService.updateReservationSettings(shopId, List.of(req));

    assertThat(setting.getAvailableStartTime()).isEqualTo(LocalTime.of(10, 0));
    assertThat(setting.getDeletedAt()).isNull();
  }

  @Test
  void 새로운_설정이_insert된다() {
    Long shopId = 1L;
    ReservationSettingId id = new ReservationSettingId(shopId, 2);

    when(reservationSettingRepository.findAllSettingsIncludingDeleted(shopId))
        .thenReturn(List.of());
    when(reservationSettingRepository.findById(id)).thenReturn(Optional.empty());

    UpdateReservationSettingRequest req =
        new UpdateReservationSettingRequest(2, LocalTime.of(9, 0), LocalTime.of(17, 0), null, null);

    reservationSettingCommandService.updateReservationSettings(shopId, List.of(req));

    verify(reservationSettingRepository).save(any());
  }

  @Test
  void 점심시간이_예약시간을_벗어나면_예외가_발생한다() {
    Long shopId = 1L;
    ReservationSettingId id = new ReservationSettingId(shopId, 3);

    ReservationSetting setting =
        ReservationSetting.builder()
            .id(id)
            .availableStartTime(LocalTime.of(10, 0))
            .availableEndTime(LocalTime.of(18, 0))
            .lunchStartTime(LocalTime.of(12, 0))
            .lunchEndTime(LocalTime.of(13, 0))
            .build();

    UpdateReservationSettingRequest req =
        new UpdateReservationSettingRequest(
            3, LocalTime.of(10, 0), LocalTime.of(18, 0), LocalTime.of(9, 0), LocalTime.of(9, 30));

    given(reservationSettingRepository.findAllSettingsIncludingDeleted(shopId))
        .willReturn(List.of(setting));
    given(reservationSettingRepository.findById(id)).willReturn(Optional.of(setting));

    assertThatThrownBy(
            () -> reservationSettingCommandService.updateReservationSettings(shopId, List.of(req)))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("점심시간은 예약 가능 시간 범위 내여야 합니다");
  }

  @Test
  void 예약시간_시작이_종료보다_늦으면_예외가_발생한다() {
    Long shopId = 1L;
    ReservationSettingId id = new ReservationSettingId(shopId, 1);
    ReservationSetting setting =
        ReservationSetting.builder()
            .id(id)
            .availableStartTime(LocalTime.of(9, 0))
            .availableEndTime(LocalTime.of(18, 0))
            .build();

    UpdateReservationSettingRequest req =
        new UpdateReservationSettingRequest(
            1, LocalTime.of(18, 0), LocalTime.of(10, 0), null, null);

    given(reservationSettingRepository.findAllSettingsIncludingDeleted(shopId))
        .willReturn(List.of(setting));
    given(reservationSettingRepository.findById(id)).willReturn(Optional.of(setting));

    assertThatThrownBy(
            () -> reservationSettingCommandService.updateReservationSettings(shopId, List.of(req)))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("예약 시작 시간은 종료 시간보다 빨라야 합니다");
  }

  @Test
  void 점심시간_시작이_종료보다_늦으면_예외가_발생한다() {
    Long shopId = 1L;
    ReservationSettingId id = new ReservationSettingId(shopId, 2);
    ReservationSetting setting =
        ReservationSetting.builder()
            .id(id)
            .availableStartTime(LocalTime.of(9, 0))
            .availableEndTime(LocalTime.of(18, 0))
            .build();

    UpdateReservationSettingRequest req =
        new UpdateReservationSettingRequest(
            2, LocalTime.of(9, 0), LocalTime.of(18, 0), LocalTime.of(13, 0), LocalTime.of(12, 30));

    given(reservationSettingRepository.findAllSettingsIncludingDeleted(shopId))
        .willReturn(List.of(setting));
    given(reservationSettingRepository.findById(id)).willReturn(Optional.of(setting));

    assertThatThrownBy(
            () -> reservationSettingCommandService.updateReservationSettings(shopId, List.of(req)))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("점심시간 시작은 종료보다 빨라야 합니다");
  }
}
