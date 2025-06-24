package com.deveagles.be15_deveagles_be.features.schedules.query.mapper;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationSettingResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReservationSettingMapper {
  List<ReservationSettingResponse> findSettingsWithUnitByShopId(Long shopId);
}
