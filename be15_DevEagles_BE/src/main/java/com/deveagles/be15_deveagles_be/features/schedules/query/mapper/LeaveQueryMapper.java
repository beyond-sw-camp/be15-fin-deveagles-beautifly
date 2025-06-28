package com.deveagles.be15_deveagles_be.features.schedules.query.mapper;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.LeaveDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.LeaveListResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.RegularLeaveDetailResponse;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LeaveQueryMapper {

  List<LeaveListResponse> findLeaves(
      @Param("shopId") Long shopId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("leaveType") String leaveType,
      @Param("staffId") Long staffId,
      @Param("limit") int limit,
      @Param("offset") int offset);

  int countLeaves(
      @Param("shopId") Long shopId,
      @Param("from") LocalDate from,
      @Param("to") LocalDate to,
      @Param("leaveType") String leaveType,
      @Param("staffId") Long staffId);

  LeaveDetailResponse findLeaveDetailById(@Param("leaveId") Long leaveId);

  RegularLeaveDetailResponse findRegularLeaveDetailById(
      @Param("regularLeaveId") Long regularLeaveId);
}
