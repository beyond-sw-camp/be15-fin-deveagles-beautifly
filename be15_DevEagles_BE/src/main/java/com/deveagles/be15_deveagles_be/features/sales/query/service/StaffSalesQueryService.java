package com.deveagles.be15_deveagles_be.features.sales.query.service;

import com.deveagles.be15_deveagles_be.features.sales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffSalesListResult;

public interface StaffSalesQueryService {
  StaffSalesListResult getStaffSales(Long shopId, GetStaffSalesListRequest request);
}
