package com.deveagles.be15_deveagles_be.features.statistics.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.AdvancedSalesStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.SalesStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.SalesSummaryResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.StatisticsRequest;
import com.deveagles.be15_deveagles_be.features.statistics.query.service.StatisticsQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Statistics", description = "통계 관련 API")
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

  private final StatisticsQueryService statisticsQueryService;

  @GetMapping("/sales")
  @Operation(summary = "매출 통계 조회", description = "기간별 매출 통계를 조회합니다.")
  public ResponseEntity<ApiResponse<List<SalesStatisticsResponse>>> getSalesStatistics(
      @AuthenticationPrincipal CustomUser customUser,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    List<SalesStatisticsResponse> statistics =
        statisticsQueryService.getSalesStatistics(customUser.getShopId(), startDate, endDate);
    return ResponseEntity.ok(ApiResponse.success(statistics));
  }

  @PostMapping("/sales/advanced")
  @Operation(summary = "고급 매출 통계 조회", description = "다양한 조건으로 그룹화하고 필터링된 매출 통계를 조회합니다.")
  public ResponseEntity<ApiResponse<List<AdvancedSalesStatisticsResponse>>>
      getAdvancedSalesStatistics(
          @AuthenticationPrincipal CustomUser customUser, @RequestBody StatisticsRequest request) {
    List<AdvancedSalesStatisticsResponse> statistics =
        statisticsQueryService.getAdvancedSalesStatistics(customUser.getShopId(), request);
    return ResponseEntity.ok(ApiResponse.success(statistics));
  }

  @GetMapping("/sales/summary")
  @Operation(summary = "매출 요약 통계 조회", description = "총 매출, 일일 평균, 거래 건수 등의 요약 통계를 조회합니다.")
  public ResponseEntity<ApiResponse<SalesSummaryResponse>> getSalesSummary(
      @AuthenticationPrincipal CustomUser customUser,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    SalesSummaryResponse summary =
        statisticsQueryService.getSalesSummary(customUser.getShopId(), startDate, endDate);
    return ResponseEntity.ok(ApiResponse.success(summary));
  }
}
