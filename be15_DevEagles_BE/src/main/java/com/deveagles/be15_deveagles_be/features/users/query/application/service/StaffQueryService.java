package com.deveagles.be15_deveagles_be.features.users.query.application.service;

import com.deveagles.be15_deveagles_be.features.users.query.application.dto.response.StaffsListResponse;

public interface StaffQueryService {
  StaffsListResponse getStaff(Long shopId, int size, int page, String keyword, Boolean active);
}
