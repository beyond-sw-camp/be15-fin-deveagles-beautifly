package com.deveagles.be15_deveagles_be.features.sales.query.service;

import com.deveagles.be15_deveagles_be.features.sales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffSalesListResponse;
import java.util.List;

public interface StaffSalesQueryService {
  List<StaffSalesListResponse> getStaffSales(Long shopId, GetStaffSalesListRequest request);
}
