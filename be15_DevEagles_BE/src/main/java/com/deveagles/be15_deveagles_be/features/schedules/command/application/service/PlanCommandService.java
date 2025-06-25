package com.deveagles.be15_deveagles_be.features.schedules.command.application.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.CreatePlanRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.CreateRegularPlanRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.DeleteScheduleRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.UpdateScheduleRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.Plan;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.RegularPlan;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ScheduleType;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.PlanRepository;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.RegularPlanRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlanCommandService {

  private final PlanRepository planRepository;
  private final RegularPlanRepository regularPlanRepository;

  public Long createPlan(CreatePlanRequest request) {
    if (request.planStartAt().isAfter(request.planEndAt())) {
      throw new BusinessException(ErrorCode.INVALID_RESERVATION_TIME_RANGE);
    }

    Plan plan =
        Plan.builder()
            .staffId(request.staffId())
            .shopId(request.shopId())
            .planTitle(request.planTitle())
            .planMemo(request.planMemo())
            .planStartAt(request.planStartAt())
            .planEndAt(request.planEndAt())
            .build();
    return planRepository.save(plan).getPlanId();
  }

  public Long createRegularPlan(CreateRegularPlanRequest request) {
    if (request.regularPlanStartAt().isAfter(request.regularPlanEndAt())) {
      throw new BusinessException(ErrorCode.INVALID_RESERVATION_TIME_RANGE);
    }
    boolean hasWeekly = request.weeklyPlan() != null;
    boolean hasMonthly = request.monthlyPlan() != null;

    if (hasWeekly == hasMonthly) {
      throw new BusinessException(ErrorCode.INVALID_SCHEDULE_REPEAT_TYPE);
    }
    RegularPlan regularPlan =
        RegularPlan.builder()
            .staffId(request.staffId())
            .shopId(request.shopId())
            .regularPlanTitle(request.regularPlanTitle())
            .monthlyPlan(request.monthlyPlan())
            .weeklyPlan(request.weeklyPlan())
            .regularPlanMemo(request.regularPlanMemo())
            .regularPlanStartAt(request.regularPlanStartAt())
            .regularPlanEndAt(request.regularPlanEndAt())
            .build();
    return regularPlanRepository.save(regularPlan).getRegularPlanId();
  }

  // 일정 (단기, 정기) 다건 삭제
  @Transactional
  public void deleteMixedSchedules(List<DeleteScheduleRequest> requests) {
    if (requests == null || requests.isEmpty()) return;

    List<Long> planIds = new ArrayList<>();
    List<Long> regularPlanIds = new ArrayList<>();

    for (DeleteScheduleRequest req : requests) {
      String type = req.type().toLowerCase(Locale.ROOT);
      if ("plan".equals(type)) {
        planIds.add(req.id());
      } else if ("regular".equals(type)) {
        regularPlanIds.add(req.id());
      } else {
        throw new BusinessException(ErrorCode.INVALID_SCHEDULE_TYPE);
      }
    }

    if (!planIds.isEmpty()) {
      List<Plan> plans = planRepository.findAllById(planIds);
      if (plans.size() != planIds.size()) {
        throw new BusinessException(ErrorCode.PLAN_NOT_FOUND);
      }
      planRepository.deleteAllInBatch(plans);
    }

    if (!regularPlanIds.isEmpty()) {
      List<RegularPlan> regularPlans = regularPlanRepository.findAllById(regularPlanIds);
      if (regularPlans.size() != regularPlanIds.size()) {
        throw new BusinessException(ErrorCode.REGULAR_PLAN_NOT_FOUND);
      }
      regularPlanRepository.deleteAllInBatch(regularPlans);
    }
  }

  @Transactional
  public void updatePlan(Long planId, CreatePlanRequest request) {
    Plan plan =
        planRepository
            .findById(planId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PLAN_NOT_FOUND));

    // 유효성 검사
    if (request.planStartAt().isAfter(request.planEndAt())) {
      throw new BusinessException(ErrorCode.INVALID_RESERVATION_TIME_RANGE);
    }

    // 값 수정
    plan.update(
        request.planTitle(), request.planMemo(), request.planStartAt(), request.planEndAt());
  }

  @Transactional
  public void updateRegularPlan(Long regularPlanId, CreateRegularPlanRequest request) {
    RegularPlan regularPlan =
        regularPlanRepository
            .findById(regularPlanId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REGULAR_PLAN_NOT_FOUND));

    if (request.regularPlanStartAt().isAfter(request.regularPlanEndAt())) {
      throw new BusinessException(ErrorCode.INVALID_RESERVATION_TIME_RANGE);
    }

    boolean hasWeekly = request.weeklyPlan() != null;
    boolean hasMonthly = request.monthlyPlan() != null;

    if (hasWeekly == hasMonthly) {
      throw new BusinessException(ErrorCode.INVALID_SCHEDULE_REPEAT_TYPE);
    }

    regularPlan.update(
        request.regularPlanTitle(),
        request.monthlyPlan(),
        request.weeklyPlan(),
        request.regularPlanMemo(),
        request.regularPlanStartAt(),
        request.regularPlanEndAt());
  }

  @Transactional
  public void switchSchedule(UpdateScheduleRequest request) {
    ScheduleType fromType = request.fromType();
    ScheduleType toType = request.toType();

    // 타입이 같으면 수정
    if (fromType == toType) {
      switch (fromType) {
        case PLAN -> updatePlan(request.fromId(), request.planRequest());
        case REGULAR -> updateRegularPlan(request.fromId(), request.regularPlanRequest());
        default -> throw new BusinessException(ErrorCode.INVALID_SCHEDULE_TYPE);
      }
      return;
    }

    // 타입이 다르면 기존 삭제 + 새로 등록
    switch (fromType) {
      case PLAN -> {
        if (!planRepository.existsById(request.fromId())) {
          throw new BusinessException(ErrorCode.PLAN_NOT_FOUND);
        }
        planRepository.deleteById(request.fromId());
      }
      case REGULAR -> {
        if (!regularPlanRepository.existsById(request.fromId())) {
          throw new BusinessException(ErrorCode.REGULAR_PLAN_NOT_FOUND);
        }
        regularPlanRepository.deleteById(request.fromId());
      }
      default -> throw new BusinessException(ErrorCode.INVALID_SCHEDULE_TYPE);
    }

    switch (toType) {
      case PLAN -> createPlan(request.planRequest());
      case REGULAR -> createRegularPlan(request.regularPlanRequest());
      default -> throw new BusinessException(ErrorCode.INVALID_SCHEDULE_TYPE);
    }
  }
}
