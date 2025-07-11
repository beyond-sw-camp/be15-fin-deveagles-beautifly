package com.deveagles.be15_deveagles_be.features.statistics.query.service;

import com.deveagles.be15_deveagles_be.features.statistics.query.dto.AdvancedSalesStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.SalesStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.SalesSummaryResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.StatisticsRequest;
import java.time.LocalDate;
import java.util.List;

public interface StatisticsQueryService {
  List<SalesStatisticsResponse> getSalesStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate);

  List<AdvancedSalesStatisticsResponse> getAdvancedSalesStatistics(
      Long shopId, StatisticsRequest request);

  SalesSummaryResponse getSalesSummary(Long shopId, LocalDate startDate, LocalDate endDate);
}
