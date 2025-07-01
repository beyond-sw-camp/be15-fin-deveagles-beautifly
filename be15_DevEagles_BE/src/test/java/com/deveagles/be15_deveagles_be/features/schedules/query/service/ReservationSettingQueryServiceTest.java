package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.CustomerReservationSettingResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationSettingResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.ReservationSettingMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("예약 설정 조회 테스트")
class ReservationSettingQueryServiceTest {

  @Mock private ReservationSettingMapper reservationSettingMapper;

  @InjectMocks private ReservationSettingQueryService reservationSettingQueryService;

  @Test
  void shopId로_예약설정리스트를_조회할수있다() {
    // given
    Long shopId = 1L;
    List<ReservationSettingResponse> mockResult =
        List.of(
            new ReservationSettingResponse(
                shopId,
                1,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                30));
    when(reservationSettingMapper.findSettingsWithUnitByShopId(shopId)).thenReturn(mockResult);

    // when
    List<ReservationSettingResponse> result =
        reservationSettingQueryService.getReservationSettings(shopId);

    // then
    assertThat(result).isNotEmpty();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getShopId()).isEqualTo(shopId);

    verify(reservationSettingMapper, times(1)).findSettingsWithUnitByShopId(shopId);
  }

  @Test
  @DisplayName("shopId와 날짜로 고객 예약 설정을 조회할 수 있다")
  void shopId와_날짜로_고객_예약설정을_조회할수있다() {
    // given
    Long shopId = 1L;
    LocalDate date = LocalDate.of(2025, 7, 1); // 화요일
    int dayOfWeek = date.getDayOfWeek().getValue(); // 2

    CustomerReservationSettingResponse expectedResponse =
        new CustomerReservationSettingResponse(
            LocalTime.of(9, 0), LocalTime.of(18, 0), LocalTime.of(12, 0), LocalTime.of(13, 0), 30);

    when(reservationSettingMapper.findCustomerReservationSetting(shopId, dayOfWeek))
        .thenReturn(expectedResponse);

    // when
    CustomerReservationSettingResponse result =
        reservationSettingQueryService.getReservationSetting(shopId, date);

    // then
    assertThat(result).isNotNull();
    assertThat(result.availableStartTime()).isEqualTo(LocalTime.of(9, 0));
    assertThat(result.availableEndTime()).isEqualTo(LocalTime.of(18, 0));
    assertThat(result.lunchStartTime()).isEqualTo(LocalTime.of(12, 0));
    assertThat(result.lunchEndTime()).isEqualTo(LocalTime.of(13, 0));
    assertThat(result.reservationTerm()).isEqualTo(30);

    verify(reservationSettingMapper, times(1)).findCustomerReservationSetting(shopId, dayOfWeek);
  }
}
