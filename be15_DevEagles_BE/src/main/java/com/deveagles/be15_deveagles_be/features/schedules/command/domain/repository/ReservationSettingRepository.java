package com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationSetting;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationSettingId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationSettingRepository
    extends JpaRepository<ReservationSetting, ReservationSettingId> {

  @Query("SELECT r FROM ReservationSetting r WHERE r.id.shopId = :shopId AND r.deletedAt IS NULL")
  List<ReservationSetting> findAllByShopId(@Param("shopId") Long shopId);

  @Query(
      "SELECT r FROM ReservationSetting r WHERE r.id.shopId = :shopId AND r.id.availableDay = :day AND r.deletedAt IS NULL")
  Optional<ReservationSetting> findByShopIdAndAvailableDay(
      @Param("shopId") Long shopId, @Param("day") int day);

  @Query("SELECT r FROM ReservationSetting r WHERE r.id.shopId = :shopId")
  List<ReservationSetting> findAllSettingsIncludingDeleted(@Param("shopId") Long shopId);
}
