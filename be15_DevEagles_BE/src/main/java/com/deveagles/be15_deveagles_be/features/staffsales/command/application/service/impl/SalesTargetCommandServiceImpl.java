package com.deveagles.be15_deveagles_be.features.staffsales.command.application.service.impl;

import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.StaffSalesTargetInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request.SetSalesTargetRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.service.SalesTargetCommandService;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.SalesTarget;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.StaffSalesSettingType;
import com.deveagles.be15_deveagles_be.features.staffsales.command.repository.SalesTargetRepository;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesTargetCommandServiceImpl implements SalesTargetCommandService {

  private final SalesTargetRepository salesTargetRepository;

  @Override
  public void setSalesTarget(Long shopId, SetSalesTargetRequest request) {

    YearMonth ym = YearMonth.from(request.targetDate());

    // 1. 기존 데이터 조회
    List<SalesTarget> existing =
        salesTargetRepository.findByShopIdAndYearMonth(shopId, ym.getYear(), ym.getMonthValue());

    // 2. BULK 설정 처리
    if (request.type() == StaffSalesSettingType.BULK) {
      // 기존 STAFF 설정 비활성화
      existing.stream().filter(e -> e.getStaffId() != null).forEach(e -> e.setStatus(false));

      // BULK 데이터 저장
      request
          .targetAmounts()
          .forEach(
              (category, amount) -> {
                Optional<SalesTarget> existingBulk =
                    existing.stream()
                        .filter(
                            e ->
                                e.getStaffId() == null
                                    && ((category.equals("membership") && e.isMembership())
                                        || (category.equals("items") && e.isItems())))
                        .findFirst();

                if (existingBulk.isPresent()) {
                  SalesTarget target = existingBulk.get();
                  target.setSalesAmount(amount);
                  target.setStatus(true);
                  salesTargetRepository.save(target);
                } else {
                  createSalesTarget(shopId, null, ym, category, amount);
                }
              });
    } else if (request.type() == StaffSalesSettingType.STAFF) {
      for (StaffSalesTargetInfo info : request.staffTargets()) {

        Long staffId = info.getStaffId();
        Map<String, Integer> amounts = info.getTargetAmounts();

        for (Map.Entry<String, Integer> entry : amounts.entrySet()) {
          String category = entry.getKey();
          Integer amount = entry.getValue();

          Optional<SalesTarget> existingStaffTarget =
              existing.stream()
                  .filter(
                      e ->
                          Objects.equals(e.getStaffId(), staffId)
                              && ((category.equals("membership") && e.isMembership())
                                  || (category.equals("items") && e.isItems())))
                  .findFirst();

          if (existingStaffTarget.isPresent()) {
            SalesTarget target = existingStaffTarget.get();
            target.setSalesAmount(amount);
            target.setStatus(true);
            salesTargetRepository.save(target);
          } else {
            createSalesTarget(shopId, staffId, ym, category, amount);
          }
        }
      }
    }
  }

  private void createSalesTarget(
      Long shopId, Long staffId, YearMonth ym, String category, Integer amount) {

    SalesTarget newTarget =
        SalesTarget.builder()
            .shopId(shopId)
            .staffId(staffId)
            .targetYear(ym.getYear())
            .targetMonth(ym.getMonthValue())
            .applyStatus(true)
            .sales(amount)
            .membership(category.equals("membership"))
            .items(category.equals("items"))
            .build();

    salesTargetRepository.save(newTarget);
  }
}
