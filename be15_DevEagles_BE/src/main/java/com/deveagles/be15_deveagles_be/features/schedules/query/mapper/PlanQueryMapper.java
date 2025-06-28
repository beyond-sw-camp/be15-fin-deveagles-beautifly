package com.deveagles.be15_deveagles_be.features.schedules.query.mapper;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanListResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PlanQueryMapper {

  PlanDetailResponse findPlanDetailById(@Param("planId") Long planId);

  List<PlanListResponse> findPlans(
      @Param("staffId") Long staffId,
      @Param("shopId") Long shopId,
      @Param("planType") String planType,
      @Param("from") LocalDateTime from, // 단기 일정에서만 사용
      @Param("to") LocalDateTime to,
      @Param("offset") int offset,
      @Param("limit") int limit);

  int countPlans(
      @Param("staffId") Long staffId,
      @Param("shopId") Long shopId,
      @Param("planType") String planType,
      @Param("from") LocalDateTime from, // 단기 일정에서만 사용
      @Param("to") LocalDateTime to);
}
