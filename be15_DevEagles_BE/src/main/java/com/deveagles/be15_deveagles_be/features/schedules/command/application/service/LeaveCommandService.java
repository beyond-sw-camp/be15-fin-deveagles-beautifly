package com.deveagles.be15_deveagles_be.features.schedules.command.application.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.*;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.Leave;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.RegularLeave;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ScheduleType;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.LeaveRepository;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.RegularLeaveRepository;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LeaveCommandService {

  private final LeaveRepository leaveRepository;
  private final RegularLeaveRepository regularLeaveRepository;

  @Transactional
  public Long createLeave(CreateLeaveRequest request) {
    Leave leave =
        Leave.builder()
            .staffId(request.staffId())
            .shopId(request.shopId())
            .leaveTitle(request.leaveTitle())
            .leaveAt(request.leaveAt())
            .leaveMemo(request.leaveMemo())
            .build();
    return leaveRepository.save(leave).getLeaveId();
  }

  @Transactional
  public void updateLeave(Long leaveId, UpdateLeaveRequest request) {
    Leave leave =
        leaveRepository
            .findById(leaveId)
            .orElseThrow(() -> new BusinessException(ErrorCode.LEAVE_NOT_FOUND));

    leave.update(request.leaveTitle(), request.leaveAt(), request.leaveMemo());
  }

  @Transactional
  public Long createRegularLeave(CreateRegularLeaveRequest request) {
    boolean hasMonthly = request.monthlyLeave() != null;
    boolean hasWeekly = request.weeklyLeave() != null;

    if (hasMonthly == hasWeekly) {
      throw new BusinessException(ErrorCode.INVALID_SCHEDULE_REPEAT_TYPE);
    }

    RegularLeave regularLeave =
        RegularLeave.builder()
            .staffId(request.staffId())
            .shopId(request.shopId())
            .regularLeaveTitle(request.regularLeaveTitle())
            .monthlyLeave(request.monthlyLeave())
            .weeklyLeave(request.weeklyLeave())
            .regularLeaveMemo(request.regularLeaveMemo())
            .build();

    return regularLeaveRepository.save(regularLeave).getRegularLeaveId();
  }

  @Transactional
  public void updateRegularLeave(Long regularLeaveId, UpdateRegularLeaveRequest request) {
    RegularLeave regularLeave =
        regularLeaveRepository
            .findById(regularLeaveId)
            .orElseThrow(() -> new BusinessException(ErrorCode.REGULAR_LEAVE_NOT_FOUND));

    regularLeave.update(
        request.regularLeaveTitle(),
        request.monthlyLeave(),
        request.weeklyLeave(),
        request.regularLeaveMemo());
  }

  @Transactional
  public void deleteMixedLeaves(List<DeleteScheduleRequest> requests) {
    if (requests == null || requests.isEmpty()) return;

    List<Long> leaveIds = new ArrayList<>();
    List<Long> regularLeaveIds = new ArrayList<>();

    for (DeleteScheduleRequest req : requests) {
      String type = req.type().toLowerCase(Locale.ROOT);
      if ("leave".equals(type)) {
        leaveIds.add(req.id());
      } else if ("regular".equals(type)) {
        regularLeaveIds.add(req.id());
      } else {
        throw new BusinessException(ErrorCode.INVALID_SCHEDULE_TYPE);
      }
    }

    if (!leaveIds.isEmpty()) {
      List<Leave> leaves = leaveRepository.findAllById(leaveIds);
      if (leaves.size() != leaveIds.size()) {
        throw new BusinessException(ErrorCode.LEAVE_NOT_FOUND);
      }
      leaveRepository.deleteAllInBatch(leaves);
    }

    if (!regularLeaveIds.isEmpty()) {
      List<RegularLeave> regularLeaves = regularLeaveRepository.findAllById(regularLeaveIds);
      if (regularLeaves.size() != regularLeaveIds.size()) {
        throw new BusinessException(ErrorCode.REGULAR_LEAVE_NOT_FOUND);
      }
      regularLeaveRepository.deleteAllInBatch(regularLeaves);
    }
  }

  @Transactional
  public void switchSchedule(UpdateLeaveScheduleRequest request) {
    ScheduleType fromType = request.fromType();
    ScheduleType toType = request.toType();

    if (fromType == toType) {
      switch (fromType) {
        case LEAVE -> updateLeave(request.fromId(), request.leaveRequest().toUpdateRequest());
        case REGULAR_LEAVE ->
            updateRegularLeave(request.fromId(), request.regularLeaveRequest().toUpdateRequest());
        default -> throw new BusinessException(ErrorCode.INVALID_SCHEDULE_TYPE);
      }
      return;
    }

    switch (fromType) {
      case LEAVE -> {
        if (!leaveRepository.existsById(request.fromId())) {
          throw new BusinessException(ErrorCode.LEAVE_NOT_FOUND);
        }
        leaveRepository.deleteById(request.fromId());
      }
      case REGULAR_LEAVE -> {
        if (!regularLeaveRepository.existsById(request.fromId())) {
          throw new BusinessException(ErrorCode.REGULAR_LEAVE_NOT_FOUND);
        }
        regularLeaveRepository.deleteById(request.fromId());
      }
      default -> throw new BusinessException(ErrorCode.INVALID_SCHEDULE_TYPE);
    }

    switch (toType) {
      case LEAVE -> createLeave(request.leaveRequest());
      case REGULAR_LEAVE -> createRegularLeave(request.regularLeaveRequest());
      default -> throw new BusinessException(ErrorCode.INVALID_SCHEDULE_TYPE);
    }
  }
}
