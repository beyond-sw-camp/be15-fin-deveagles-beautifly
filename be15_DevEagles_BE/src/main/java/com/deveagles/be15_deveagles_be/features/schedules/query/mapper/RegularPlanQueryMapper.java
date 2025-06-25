package com.deveagles.be15_deveagles_be.features.schedules.query.mapper;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.RegularPlanDetailResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RegularPlanQueryMapper {
  RegularPlanDetailResponse findRegularPlanDetailById(@Param("regularPlanId") Long regularPlanId);
}
