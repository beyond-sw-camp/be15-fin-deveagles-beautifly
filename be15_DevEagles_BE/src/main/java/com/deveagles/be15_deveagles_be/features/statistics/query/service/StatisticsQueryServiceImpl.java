package com.deveagles.be15_deveagles_be.features.statistics.query.service;

import com.deveagles.be15_deveagles_be.features.statistics.query.dto.AdvancedSalesStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.SalesStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.SalesSummaryResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.StatisticsRequest;
import com.deveagles.be15_deveagles_be.features.statistics.query.repository.StatisticsQueryRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsQueryServiceImpl implements StatisticsQueryService {

  private final StatisticsQueryRepository statisticsQueryRepository;

  @Override
  public List<SalesStatisticsResponse> getSalesStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate) {
    return statisticsQueryRepository.findSalesStatisticsByPeriod(shopId, startDate, endDate);
  }

  @Override
  public List<AdvancedSalesStatisticsResponse> getAdvancedSalesStatistics(
      Long shopId, StatisticsRequest request) {
    return statisticsQueryRepository.findAdvancedSalesStatistics(shopId, request);
  }

  @Override
  public SalesSummaryResponse getSalesSummary(Long shopId, LocalDate startDate, LocalDate endDate) {
    return statisticsQueryRepository.findSalesSummary(shopId, startDate, endDate);
  }
}
