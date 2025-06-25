package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanListResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.RegularPlanDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.PlanQueryMapper;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.RegularPlanQueryMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PlanQueryService {

  private final PlanQueryMapper planQueryMapper;
  private final RegularPlanQueryMapper regularPlanQueryMapper;

  public PlanDetailResponse getPlanDetail(Long planId) {
    PlanDetailResponse result = planQueryMapper.findPlanDetailById(planId);
    if (result == null) throw new BusinessException(ErrorCode.PLAN_NOT_FOUND);
    return result;
  }

  public RegularPlanDetailResponse getRegularPlanDetail(Long regularPlanId) {
    RegularPlanDetailResponse result =
        regularPlanQueryMapper.findRegularPlanDetailById(regularPlanId);
    if (result == null) throw new BusinessException(ErrorCode.REGULAR_PLAN_NOT_FOUND);
    return result;
  }

  public PagedResponse<PlanListResponse> getPlanList(
      Long staffId, Long shopId, String planType, int page, int size) {
    int offset = page * size;

    List<PlanListResponse> content =
        planQueryMapper.findPlans(staffId, shopId, planType, offset, size);
    int total = planQueryMapper.countPlans(staffId, shopId, planType);

    Pagination pagination =
        Pagination.builder()
            .currentPage(page)
            .totalPages((int) Math.ceil((double) total / size))
            .totalItems(total)
            .build();

    return new PagedResponse<>(content, pagination);
  }
}
