package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.CustomerReservationSettingResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationSettingResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.ReservationSettingMapper;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationSettingQueryService {
  private final ReservationSettingMapper reservationSettingMapper;

  public List<ReservationSettingResponse> getReservationSettings(Long shopId) {
    List<ReservationSettingResponse> settings =
        reservationSettingMapper.findSettingsWithUnitByShopId(shopId);

    if (settings == null || settings.isEmpty()) {
      throw new BusinessException(ErrorCode.RESERVATION_SETTING_NOT_FOUND);
    }

    return settings;
  }

  public CustomerReservationSettingResponse getReservationSetting(Long shopId, LocalDate date) {
    int dayOfWeek = date.getDayOfWeek().getValue(); // 월=1 ~ 일=7
    return reservationSettingMapper.findCustomerReservationSetting(shopId, dayOfWeek);
  }
}
