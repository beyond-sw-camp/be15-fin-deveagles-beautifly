package com.deveagles.be15_deveagles_be.features.staffsales.command.application.service.impl;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.IncentiveListResult;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.ProductIncentiveRates;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.StaffIncentiveInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.StaffSimpleInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.service.IncentiveCommandService;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.Incentive;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.IncentiveType;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.staffsales.command.repository.IncentiveRepository;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IncentiveCommandServiceImpl implements IncentiveCommandService {

  private final ShopRepository shopRepository;
  private final UserRepository userRepository;
  private final IncentiveRepository incentiveRepository;

  @Override
  @Transactional(readOnly = true)
  public IncentiveListResult getIncentives(Long shopId) {

    // 1. 매장 인센티브 활성화 여부 조회
    boolean incentiveEnabled = shopRepository.findIncentiveStatusByShopId(shopId).orElse(false);

    // 2. 직원 목록 조회
    List<Staff> staffList = userRepository.findByShopIdAndLeftDateIsNull(shopId);

    // 3. 매장 인센티브 조회
    List<Incentive> incentives = incentiveRepository.findByShopId(shopId);

    // 4. 직원 목록 변환
    List<StaffSimpleInfo> staffSimpleList =
        staffList.stream()
            .map(
                staff ->
                    StaffSimpleInfo.builder()
                        .staffId(staff.getStaffId())
                        .staffName(staff.getStaffName())
                        .build())
            .toList();

    // 5. 직원별 인센티브 여부 판단
    boolean isStaffBased = incentives.stream().anyMatch(i -> i.getStaffId() != null);
    IncentiveType type = isStaffBased ? IncentiveType.STAFF : IncentiveType.BULK;

    // 6. 직원별 인센티브
    List<StaffIncentiveInfo> incentiveInfoList =
        buildStaffIncentiveInfoList(incentives, staffList, type);

    return IncentiveListResult.builder()
        .shopId(shopId)
        .incentiveEnabled(incentiveEnabled)
        .incentiveType(type)
        .staffList(staffSimpleList)
        .incentiveList(incentiveInfoList)
        .build();
  }

  private List<StaffIncentiveInfo> buildStaffIncentiveInfoList(
      List<Incentive> incentives, List<Staff> staffList, IncentiveType type) {

    if (type == IncentiveType.BULK) {
      // 일괄 인센티브만 필터
      Map<PaymentsMethod, ProductIncentiveRates> rateMap = mapIncentivesToRate(incentives, null);

      return List.of(
          StaffIncentiveInfo.builder().staffId(null).staffName(null).incentives(rateMap).build());
    }

    // 직원별
    return staffList.stream()
        .map(
            staff -> {
              Map<PaymentsMethod, ProductIncentiveRates> rates =
                  mapIncentivesToRate(incentives, staff.getStaffId());

              // 해당 직원 인센티브 없으면 일괄 적용
              if (rates.isEmpty()) {
                rates = mapIncentivesToRate(incentives, null);
              }

              return StaffIncentiveInfo.builder()
                  .staffId(staff.getStaffId())
                  .staffName(staff.getStaffName())
                  .incentives(rates.isEmpty() ? createZeroRates() : rates)
                  .build();
            })
        .toList();
  }

  private Map<PaymentsMethod, ProductIncentiveRates> mapIncentivesToRate(
      List<Incentive> incentives, Long staffId) {
    return incentives.stream()
        .filter(i -> Objects.equals(i.getStaffId(), staffId))
        .collect(
            Collectors.groupingBy(
                Incentive::getPaymentsMethod,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                      Map<ProductType, Integer> productMap =
                          list.stream()
                              .collect(
                                  Collectors.toMap(Incentive::getType, Incentive::getIncentive));

                      return ProductIncentiveRates.builder()
                          .service(productMap.getOrDefault(ProductType.SERVICE, 0))
                          .product(productMap.getOrDefault(ProductType.PRODUCT, 0))
                          .sessionPass(productMap.getOrDefault(ProductType.SESSION_PASS, 0))
                          .prepaidPass(productMap.getOrDefault(ProductType.PREPAID_PASS, 0))
                          .build();
                    })));
  }

  private Map<PaymentsMethod, ProductIncentiveRates> createZeroRates() {
    return Arrays.stream(PaymentsMethod.values())
        .collect(
            Collectors.toMap(
                Function.identity(),
                method ->
                    ProductIncentiveRates.builder()
                        .service(0)
                        .product(0)
                        .sessionPass(0)
                        .prepaidPass(0)
                        .build()));
  }
}
