package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationSettingResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.ReservationSettingMapper;
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
}
