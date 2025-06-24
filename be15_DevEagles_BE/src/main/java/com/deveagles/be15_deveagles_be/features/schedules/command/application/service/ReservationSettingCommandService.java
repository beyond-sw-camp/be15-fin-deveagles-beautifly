package com.deveagles.be15_deveagles_be.features.schedules.command.application.service;

import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.UpdateReservationSettingRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationSetting;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationSettingId;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.ReservationSettingRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationSettingCommandService {
  private final ReservationSettingRepository reservationSettingRepository;

  @Transactional
  public void updateReservationSettings(
      Long shopId, List<UpdateReservationSettingRequest> requestList) {
    Set<Integer> incomingDays =
        requestList.stream()
            .map(UpdateReservationSettingRequest::availableDay)
            .collect(Collectors.toSet());

    List<ReservationSetting> allSettings =
        reservationSettingRepository.findAllSettingsIncludingDeleted(shopId);

    for (ReservationSetting setting : allSettings) {
      if (!incomingDays.contains(setting.getAvailableDay())) {
        setting.markDeleted();
      }
    }

    for (UpdateReservationSettingRequest req : requestList) {
      ReservationSettingId id = new ReservationSettingId(shopId, req.availableDay());
      Optional<ReservationSetting> optional = reservationSettingRepository.findById(id);

      if (optional.isPresent()) {
        ReservationSetting setting = optional.get();
        setting.update(
            req.availableStartTime(),
            req.availableEndTime(),
            req.lunchStartTime(),
            req.lunchEndTime());
        setting.restore();
      } else {
        ReservationSetting setting =
            ReservationSetting.builder()
                .id(id)
                .availableStartTime(req.availableStartTime())
                .availableEndTime(req.availableEndTime())
                .lunchStartTime(req.lunchStartTime())
                .lunchEndTime(req.lunchEndTime())
                .build();

        reservationSettingRepository.save(setting);
      }
    }
  }
}
