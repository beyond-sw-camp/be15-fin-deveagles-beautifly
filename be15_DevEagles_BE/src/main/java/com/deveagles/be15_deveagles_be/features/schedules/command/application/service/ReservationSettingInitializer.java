package com.deveagles.be15_deveagles_be.features.schedules.command.application.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationSetting;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationSettingId;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.ReservationSettingRepository;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationSettingInitializer {

  private final ReservationSettingRepository reservationSettingRepository;

  public void initDefault(Long shopId) {
    List<ReservationSetting> existing = reservationSettingRepository.findAllByShopId(shopId);
    if (!existing.isEmpty()) {
      throw new BusinessException(ErrorCode.RESERVATION_SETTING_ALREADY_EXISTS);
    }

    for (int day = 1; day <= 7; day++) {
      ReservationSetting setting =
          ReservationSetting.builder()
              .id(new ReservationSettingId(shopId, day))
              .availableStartTime(LocalTime.of(9, 0))
              .availableEndTime(LocalTime.of(18, 0))
              .lunchStartTime(null)
              .lunchEndTime(null)
              .build();

      reservationSettingRepository.save(setting);
    }
  }
}
