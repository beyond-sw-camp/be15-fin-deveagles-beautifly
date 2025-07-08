package com.deveagles.be15_deveagles_be.features.sales.query.service.impl;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffPaymentsSalesResponse;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffSalesListResponse;
import com.deveagles.be15_deveagles_be.features.sales.query.repository.StaffSalesQueryRepository;
import com.deveagles.be15_deveagles_be.features.sales.query.service.StaffSalesQueryService;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffSalesQueryServiceImpl implements StaffSalesQueryService {

  private final UserRepository userRepository;
  private final StaffSalesQueryRepository staffSalesQueryRepository;

  @Override
  public List<StaffSalesListResponse> getStaffSales(Long shopId, GetStaffSalesListRequest request) {

    // 1. 기간 계산
    LocalDateTime startDate = getStartDate(request);
    LocalDateTime endDate = getEndDate(request);

    // 2. 직원 리스트 조회
    List<Staff> staffList = userRepository.findByShopIdAndLeftDateIsNull(shopId);

    // 3. 직원별 매출 데이터 조회
    List<StaffSalesListResponse> result =
        staffList.stream()
            .map(
                staff -> {
                  List<StaffPaymentsSalesResponse> paymentsSalesList =
                      staffSalesQueryRepository.getSalesByStaff(
                          staff.getStaffId(), startDate, endDate);

                  return StaffSalesListResponse.builder()
                      .staffId(staff.getStaffId())
                      .staffName(staff.getStaffName())
                      .paymentsSalesList(paymentsSalesList)
                      .build();
                })
            .toList();

    return result;
  }

  private LocalDateTime getStartDate(GetStaffSalesListRequest request) {
    if (request.searchMode() == SearchMode.MONTH) {
      return request.startDate().withDayOfMonth(1).atStartOfDay();
    }
    return request.startDate().atStartOfDay();
  }

  private LocalDateTime getEndDate(GetStaffSalesListRequest request) {
    if (request.searchMode() == SearchMode.MONTH) {
      return request
          .startDate()
          .withDayOfMonth(request.startDate().lengthOfMonth())
          .atTime(23, 59, 59);
    }
    return Optional.ofNullable(request.endDate())
        .map(d -> d.atTime(23, 59, 59))
        .orElse(request.startDate().atTime(23, 59, 59));
  }
}
