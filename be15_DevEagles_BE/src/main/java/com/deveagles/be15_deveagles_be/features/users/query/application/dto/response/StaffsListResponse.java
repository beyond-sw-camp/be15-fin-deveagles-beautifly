package com.deveagles.be15_deveagles_be.features.users.query.application.dto.response;

import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffsListResponse {

  private List<StaffListInfo> staffList;
  private Pagination pagination;
}
