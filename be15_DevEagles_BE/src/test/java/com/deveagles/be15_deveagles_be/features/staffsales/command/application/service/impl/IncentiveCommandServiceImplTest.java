package com.deveagles.be15_deveagles_be.features.staffsales.command.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.ProductIncentiveRates;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.StaffIncentiveInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request.SetIncentiveRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.IncentiveListResult;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.Incentive;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.IncentiveType;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.staffsales.command.repository.IncentiveRepository;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("IncentiveCommandService 단위 테스트")
public class IncentiveCommandServiceImplTest {

  @InjectMocks private IncentiveCommandServiceImpl incentiveCommandService;

  @Mock private ShopRepository shopRepository;

  @Mock private UserRepository userRepository;

  @Mock private IncentiveRepository incentiveRepository;

  @DisplayName("getIncentives_일괄설정_반환 성공")
  @Test
  void getIncentives_일괄설정_반환() {
    // given
    Long shopId = 1L;

    given(shopRepository.findIncentiveStatusByShopId(shopId)).willReturn(Optional.of(true));

    Staff staff1 = Staff.builder().staffId(100L).staffName("위시").build();
    Staff staff2 = Staff.builder().staffId(101L).staffName("에스쿱스").build();

    given(userRepository.findByShopIdAndLeftDateIsNull(shopId)).willReturn(List.of(staff1, staff2));

    Incentive bulkIncentive1 =
        Incentive.builder()
            .shopId(shopId)
            .staffId(null)
            .paymentsMethod(PaymentsMethod.CARD)
            .type(ProductType.SERVICE)
            .incentive(10)
            .isActive(true)
            .build();

    Incentive bulkIncentive2 =
        Incentive.builder()
            .shopId(shopId)
            .staffId(null)
            .paymentsMethod(PaymentsMethod.CARD)
            .type(ProductType.PRODUCT)
            .incentive(15)
            .isActive(true)
            .build();

    given(incentiveRepository.findByShopIdAndIsActiveTrue(shopId))
        .willReturn(List.of(bulkIncentive1, bulkIncentive2));

    // when
    IncentiveListResult result = incentiveCommandService.getIncentives(shopId);

    // then
    assertThat(result.getShopId()).isEqualTo(shopId);
    assertThat(result.getIncentiveEnabled()).isTrue();
    assertThat(result.getIncentiveType()).isEqualTo(IncentiveType.BULK);
    assertThat(result.getStaffList()).hasSize(2);
    assertThat(result.getIncentiveList()).hasSize(1); // 일괄 인센티브 1개
  }

  @DisplayName("setIncentive - 일괄 설정 저장 성공")
  @Test
  void setIncentive_일괄설정_성공() {
    // given
    Long shopId = 1L;
    Shop shop = Shop.builder().shopId(shopId).incentiveStatus(false).build();

    given(shopRepository.findByShopId(shopId)).willReturn(Optional.of(shop));

    Map<PaymentsMethod, ProductIncentiveRates> rateMap =
        Map.of(
            PaymentsMethod.CARD,
            ProductIncentiveRates.builder()
                .service(10)
                .product(5)
                .sessionPass(15)
                .prepaidPass(20)
                .build());

    StaffIncentiveInfo staffIncentiveInfo =
        StaffIncentiveInfo.builder().staffId(null).staffName(null).incentives(rateMap).build();

    SetIncentiveRequest request =
        new SetIncentiveRequest(true, IncentiveType.BULK, staffIncentiveInfo);

    given(
            incentiveRepository.findStaffSpecificIncentives(
                shopId, PaymentsMethod.CARD, ProductType.SERVICE))
        .willReturn(List.of());
    given(
            incentiveRepository.findCommonIncentives(
                shopId, PaymentsMethod.CARD, ProductType.SERVICE))
        .willReturn(Optional.empty());

    // when
    incentiveCommandService.setIncentive(shopId, request);

    // then
    verify(shopRepository).save(any(Shop.class));
    verify(
            incentiveRepository,
            times(4)) /* 상품 타입 - SERVICE, PRODUCT, PREPAID_PASS, SESSION_PASS 총 4번 호출*/
        .save(any(Incentive.class));
  }

  @DisplayName("setIncentive - 직원별 설정 저장 성공")
  @Test
  void setIncentive_직원설정_성공() {
    // given
    Long shopId = 1L;
    Long staffId = 100L;

    Shop shop = Shop.builder().shopId(shopId).incentiveStatus(false).build();

    given(shopRepository.findByShopId(shopId)).willReturn(Optional.of(shop));

    Map<PaymentsMethod, ProductIncentiveRates> rateMap =
        Map.of(
            PaymentsMethod.CARD,
            ProductIncentiveRates.builder()
                .service(10)
                .product(5)
                .sessionPass(15)
                .prepaidPass(20)
                .build());

    StaffIncentiveInfo staffIncentiveInfo =
        StaffIncentiveInfo.builder().staffId(staffId).staffName("김인센").incentives(rateMap).build();

    SetIncentiveRequest request =
        new SetIncentiveRequest(true, IncentiveType.STAFF, staffIncentiveInfo);

    given(
            incentiveRepository.findByShopIdAndStaffIdAndPaymentsMethodAndType(
                shopId, staffId, PaymentsMethod.CARD, ProductType.SERVICE))
        .willReturn(Optional.empty());

    // when
    incentiveCommandService.setIncentive(shopId, request);

    // then
    verify(shopRepository).save(any(Shop.class));
    verify(incentiveRepository, times(4)).save(any(Incentive.class));
  }

  @DisplayName("setIncentive - 매장 인센티브 비활성화 시 설정 저장 없이 종료")
  @Test
  void setIncentive_비활성화_성공() {
    // given
    Long shopId = 1L;
    Shop shop = Shop.builder().shopId(shopId).incentiveStatus(true).build();

    given(shopRepository.findByShopId(shopId)).willReturn(Optional.of(shop));

    SetIncentiveRequest request = new SetIncentiveRequest(false, IncentiveType.BULK, null);

    // when
    incentiveCommandService.setIncentive(shopId, request);

    // then
    verify(shopRepository).save(any(Shop.class));
    verifyNoInteractions(incentiveRepository);
  }

  @DisplayName("setIncentive - 기존 일괄 인센티브 존재 시 업데이트")
  @Test
  void setIncentive_일괄설정_업데이트_성공() {
    // given
    Long shopId = 1L;

    Shop shop = Shop.builder().shopId(shopId).incentiveStatus(false).build();

    given(shopRepository.findByShopId(shopId)).willReturn(Optional.of(shop));

    Incentive existingIncentive =
        Incentive.builder()
            .shopId(shopId)
            .staffId(null)
            .paymentsMethod(PaymentsMethod.CARD)
            .type(ProductType.SERVICE)
            .incentive(5)
            .isActive(false)
            .build();

    Map<PaymentsMethod, ProductIncentiveRates> rateMap =
        Map.of(
            PaymentsMethod.CARD,
            ProductIncentiveRates.builder()
                .service(10)
                .product(5)
                .sessionPass(0)
                .prepaidPass(0)
                .build());

    StaffIncentiveInfo staffIncentiveInfo =
        StaffIncentiveInfo.builder().staffId(null).staffName(null).incentives(rateMap).build();

    SetIncentiveRequest request =
        new SetIncentiveRequest(true, IncentiveType.BULK, staffIncentiveInfo);

    given(
            incentiveRepository.findStaffSpecificIncentives(
                shopId, PaymentsMethod.CARD, ProductType.SERVICE))
        .willReturn(List.of());
    given(
            incentiveRepository.findCommonIncentives(
                shopId, PaymentsMethod.CARD, ProductType.SERVICE))
        .willReturn(Optional.of(existingIncentive));

    // when
    incentiveCommandService.setIncentive(shopId, request);

    // then
    verify(incentiveRepository).save(existingIncentive);
    assertThat(existingIncentive.getIncentive()).isEqualTo(10);
    assertThat(existingIncentive.isActive()).isTrue();
  }

  @DisplayName("setIncentive - 직원별 인센티브 존재 시 수정 처리")
  @Test
  void setIncentive_직원설정_업데이트_성공() {
    // given
    Long shopId = 1L;
    Long staffId = 123L;

    Shop shop = Shop.builder().shopId(shopId).incentiveStatus(false).build();

    given(shopRepository.findByShopId(shopId)).willReturn(Optional.of(shop));

    // 기존 인센티브
    Incentive existingIncentive =
        Incentive.builder()
            .shopId(shopId)
            .staffId(staffId)
            .paymentsMethod(PaymentsMethod.CARD)
            .type(ProductType.SERVICE)
            .incentive(5)
            .isActive(false)
            .build();

    Map<PaymentsMethod, ProductIncentiveRates> rateMap =
        Map.of(
            PaymentsMethod.CARD,
            ProductIncentiveRates.builder()
                .service(15) // 기존 5 → 15로 업데이트
                .product(0)
                .sessionPass(0)
                .prepaidPass(0)
                .build());

    StaffIncentiveInfo staffIncentiveInfo =
        StaffIncentiveInfo.builder().staffId(staffId).staffName("박직원").incentives(rateMap).build();

    SetIncentiveRequest request =
        new SetIncentiveRequest(true, IncentiveType.STAFF, staffIncentiveInfo);

    given(
            incentiveRepository.findByShopIdAndStaffIdAndPaymentsMethodAndType(
                shopId, staffId, PaymentsMethod.CARD, ProductType.SERVICE))
        .willReturn(Optional.of(existingIncentive));

    // when
    incentiveCommandService.setIncentive(shopId, request);

    // then
    verify(shopRepository).save(any(Shop.class));
    verify(incentiveRepository).save(existingIncentive);

    assertThat(existingIncentive.getIncentive()).isEqualTo(15);
    assertThat(existingIncentive.isActive()).isTrue();
  }
}
