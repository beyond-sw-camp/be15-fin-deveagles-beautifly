package com.deveagles.be15_deveagles_be.features.sales.query.service;

import com.deveagles.be15_deveagles_be.features.sales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffSalesDetailListResult;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffSalesListResult;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffSalesTargetListResult;

public interface StaffSalesQueryService {
  StaffSalesListResult getStaffSales(Long shopId, GetStaffSalesListRequest request);

  StaffSalesDetailListResult getStaffDetailSales(Long shopId, GetStaffSalesListRequest request);

  StaffSalesTargetListResult getStaffSalesTarget(Long shopId, GetStaffSalesListRequest request);
}
