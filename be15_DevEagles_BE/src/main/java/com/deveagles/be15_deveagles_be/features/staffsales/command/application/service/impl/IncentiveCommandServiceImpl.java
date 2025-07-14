package com.deveagles.be15_deveagles_be.features.staffsales.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.ProductIncentiveRates;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.StaffIncentiveInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request.SetIncentiveRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.IncentiveListResult;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.StaffSimpleInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.service.IncentiveCommandService;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.Incentive;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.StaffSalesSettingType;
import com.deveagles.be15_deveagles_be.features.staffsales.command.repository.IncentiveRepository;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
    List<Incentive> incentives = incentiveRepository.findByShopIdAndIsActiveTrue(shopId);

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
    StaffSalesSettingType type =
        isStaffBased ? StaffSalesSettingType.STAFF : StaffSalesSettingType.BULK;

    // 6. 직원별 인센티브
    List<StaffIncentiveInfo> incentiveInfoList =
        buildStaffIncentiveInfoList(incentives, staffList, type);

    return IncentiveListResult.builder()
        .shopId(shopId)
        .incentiveEnabled(incentiveEnabled)
        .staffSalesSettingType(type)
        .staffList(staffSimpleList)
        .incentiveList(incentiveInfoList)
        .build();
  }

  @Override
  @Transactional
  public void setIncentive(Long shopId, SetIncentiveRequest request) {

    Shop shop =
        shopRepository
            .findByShopId(shopId)
            .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUNT));

    if (Boolean.FALSE.equals(request.isActive())) {
      shop.setIncentive(false);
      shopRepository.save(shop);
      return;
    } else {
      shop.setIncentive(true);
      shopRepository.save(shop);
    }

    Map<PaymentsMethod, ProductIncentiveRates> map = request.incentiveInfo().getIncentives();
    Long staffId =
        request.type() == StaffSalesSettingType.STAFF ? request.incentiveInfo().getStaffId() : null;

    for (Map.Entry<PaymentsMethod, ProductIncentiveRates> entry : map.entrySet()) {
      PaymentsMethod method = entry.getKey();
      ProductIncentiveRates rates = entry.getValue();

      saveOrUpdateIncentive(shopId, staffId, method, ProductType.SERVICE, rates.getService());
      saveOrUpdateIncentive(shopId, staffId, method, ProductType.PRODUCT, rates.getProduct());
      saveOrUpdateIncentive(
          shopId, staffId, method, ProductType.SESSION_PASS, rates.getSessionPass());
      saveOrUpdateIncentive(
          shopId, staffId, method, ProductType.PREPAID_PASS, rates.getPrepaidPass());
    }
  }

  private List<StaffIncentiveInfo> buildStaffIncentiveInfoList(
      List<Incentive> incentives, List<Staff> staffList, StaffSalesSettingType type) {

    if (type == StaffSalesSettingType.BULK) {
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
                  .incentives(rates.isEmpty() ? ZERO_RATES : rates)
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

  private static final Map<PaymentsMethod, ProductIncentiveRates> ZERO_RATES =
      Arrays.stream(PaymentsMethod.values())
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

  private void saveOrUpdateIncentive(
      Long shopId, Long staffId, PaymentsMethod method, ProductType type, int ratio) {
    if (staffId == null) {
      // 1. 직원별 설정 isActive false 처리
      List<Incentive> staffIncentives =
          incentiveRepository.findStaffSpecificIncentives(shopId, method, type);
      for (Incentive i : staffIncentives) {
        i.setActive(false);
      }
      incentiveRepository.saveAll(staffIncentives);

      // 2. 일괄 설정 insert or update
      Optional<Incentive> existing = incentiveRepository.findCommonIncentives(shopId, method, type);

      if (existing.isPresent()) {
        existing.get().setIncentiveRatio(ratio);
        existing.get().setActive(true); // 혹시 비활성화돼 있었을 수도 있으니
        incentiveRepository.save(existing.get());
      } else {
        Incentive newIncentive =
            Incentive.builder()
                .shopId(shopId)
                .staffId(null)
                .paymentsMethod(method)
                .type(type)
                .incentive(ratio)
                .isActive(true)
                .build();
        incentiveRepository.save(newIncentive);
      }
    } else {
      // 직원별 설정
      Optional<Incentive> existing =
          incentiveRepository.findByShopIdAndStaffIdAndPaymentsMethodAndType(
              shopId, staffId, method, type);

      if (existing.isPresent()) {
        existing.get().setIncentiveRatio(ratio);
        existing.get().setActive(true);
        incentiveRepository.save(existing.get());
      } else {
        Incentive newIncentive =
            Incentive.builder()
                .shopId(shopId)
                .staffId(staffId)
                .paymentsMethod(method)
                .type(type)
                .incentive(ratio)
                .isActive(true)
                .build();
        incentiveRepository.save(newIncentive);
      }
    }
  }
}
