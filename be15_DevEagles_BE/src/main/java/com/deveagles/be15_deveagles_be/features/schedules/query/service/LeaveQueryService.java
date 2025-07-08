package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.LeaveListRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.LeaveDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.LeaveListResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.RegularLeaveDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.LeaveQueryMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaveQueryService {

  private final LeaveQueryMapper leaveQueryMapper;

  public PagedResult<LeaveListResponse> getLeaveList(Long shopId, LeaveListRequest request) {
    int offset = request.page() * request.size();

    List<LeaveListResponse> leaves =
        leaveQueryMapper.findLeaves(
            shopId,
            request.from(),
            request.to(),
            request.leaveType(),
            request.staffId(),
            request.size(),
            offset);

    int total =
        leaveQueryMapper.countLeaves(
            shopId, request.from(), request.to(), request.leaveType(), request.staffId());

    return new PagedResult<>(
        leaves,
        new Pagination(request.page(), (int) Math.ceil((double) total / request.size()), total));
  }

  public LeaveDetailResponse getLeaveDetail(Long leaveId) {
    LeaveDetailResponse result = leaveQueryMapper.findLeaveDetailById(leaveId);
    if (result == null) {
      throw new BusinessException(ErrorCode.LEAVE_NOT_FOUND);
    }
    return result;
  }

  public RegularLeaveDetailResponse getRegularLeaveDetail(Long regularLeaveId) {
    RegularLeaveDetailResponse result = leaveQueryMapper.findRegularLeaveDetailById(regularLeaveId);
    if (result == null) {
      throw new BusinessException(ErrorCode.REGULAR_LEAVE_NOT_FOUND);
    }
    return result;
  }
}
