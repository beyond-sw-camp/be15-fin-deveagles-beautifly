package com.deveagles.be15_deveagles_be.features.schedules.query.mapper;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanListResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PlanQueryMapper {

  // 단기 일정 상세 조회
  PlanDetailResponse findPlanDetailById(@Param("planId") Long planId);

  List<PlanListResponse> findPlans(
      @Param("staffId") Long staffId,
      @Param("shopId") Long shopId,
      @Param("planType") String planType,
      @Param("offset") int offset,
      @Param("limit") int limit);

  int countPlans(
      @Param("staffId") Long staffId,
      @Param("shopId") Long shopId,
      @Param("planType") String planType);
}
