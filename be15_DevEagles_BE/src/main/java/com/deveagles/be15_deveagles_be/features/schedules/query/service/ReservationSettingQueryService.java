package com.deveagles.be15_deveagles_be.features.schedules.query.service;

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
    return reservationSettingMapper.findSettingsWithUnitByShopId(shopId);
  }

  public CustomerReservationSettingResponse getReservationSetting(Long shopId, LocalDate date) {
    int dayOfWeek = date.getDayOfWeek().getValue(); // 월=1 ~ 일=7
    return reservationSettingMapper.findCustomerReservationSetting(shopId, dayOfWeek);
  }
}
