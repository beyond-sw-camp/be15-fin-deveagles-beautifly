package com.deveagles.be15_deveagles_be.features.staffsales.query.service.impl;

import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.StaffSalesTargetInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request.GetSalesTargetRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.SalesTargetListResult;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.StaffSimpleInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.SalesTarget;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.StaffSalesSettingType;
import com.deveagles.be15_deveagles_be.features.staffsales.query.repository.SalesTargetQueryRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.query.service.SalesTargetQueryService;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.time.Month;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesTargetQueryServiceImpl implements SalesTargetQueryService {

  private final UserRepository userRepository;
  private final SalesTargetQueryRepository salesTargetQueryRepository;

  @Override
  public SalesTargetListResult getSalesTarget(Long shopId, GetSalesTargetRequest request) {

    Year year = Year.of(request.targetDate().getYear());
    Month month = request.targetDate().getMonth();

    // 1. 직원 목록
    List<Staff> staffList = userRepository.findByShopIdAndLeftDateIsNull(shopId);

    List<StaffSimpleInfo> staffInfos =
        staffList.stream()
            .map(
                staff ->
                    StaffSimpleInfo.builder()
                        .staffId(staff.getStaffId())
                        .staffName(staff.getStaffName())
                        .build())
            .toList();

    // 2. 해당 년월의 목표 매출 조회
    List<SalesTarget> targetList =
        salesTargetQueryRepository.findAppliedTargets(shopId, year, month);

    // 3. 일괄 설정 확인
    boolean hasOnlyBulk = targetList.stream().allMatch(t -> t.getStaffId() == null);
    StaffSalesSettingType settingType =
        hasOnlyBulk ? StaffSalesSettingType.BULK : StaffSalesSettingType.STAFF;

    // 4. 일괄 설정 Target 추출
    Map<String, Integer> defaultTargets = new HashMap<>();
    targetList.stream()
        .filter(t -> t.getStaffId() == null)
        .forEach(
            t -> {
              if (Boolean.TRUE.equals(t.isItems())) defaultTargets.put("items", t.getSales());
              if (Boolean.TRUE.equals(t.isMembership()))
                defaultTargets.put("membership", t.getSales());
            });

    // 5. 개별 설정 Target 추출
    Map<Long, Map<String, Integer>> personalTargets = new HashMap<>();
    targetList.stream()
        .filter(t -> t.getStaffId() != null)
        .forEach(
            t -> {
              Long staffId = t.getStaffId();
              personalTargets.putIfAbsent(staffId, new HashMap<>());
              if (Boolean.TRUE.equals(t.isItems()))
                personalTargets.get(staffId).put("items", t.getSales());
              if (Boolean.TRUE.equals(t.isMembership()))
                personalTargets.get(staffId).put("membership", t.getSales());
            });

    // 6. 직원별 targetAmount
    List<StaffSalesTargetInfo> salesTargetInfos =
        staffList.stream()
            .map(
                staff -> {
                  Map<String, Integer> targetAmounts = new HashMap<>();

                  // 우선순위 적용
                  if (personalTargets.containsKey(staff.getStaffId())) {
                    targetAmounts = personalTargets.get(staff.getStaffId());
                  } else {
                    targetAmounts.put("items", defaultTargets.getOrDefault("items", 0));
                    targetAmounts.put("membership", defaultTargets.getOrDefault("membership", 0));
                  }

                  return StaffSalesTargetInfo.builder()
                      .staffId(staff.getStaffId())
                      .targetAmounts(targetAmounts)
                      .build();
                })
            .toList();

    return SalesTargetListResult.builder()
        .targetYear(year)
        .targetMonth(month)
        .type(settingType)
        .staffList(staffInfos)
        .salesTargetInfos(salesTargetInfos)
        .build();
  }
}
