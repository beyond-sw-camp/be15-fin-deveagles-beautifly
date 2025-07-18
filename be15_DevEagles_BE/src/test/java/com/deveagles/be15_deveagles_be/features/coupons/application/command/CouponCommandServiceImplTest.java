package com.deveagles.be15_deveagles_be.features.coupons.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.coupons.common.CouponDto;
import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.domain.service.CouponCodeGenerator;
import com.deveagles.be15_deveagles_be.features.coupons.infrastructure.repository.CouponJpaRepository;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.request.DeleteCouponRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponCommandService 단위 테스트")
class CouponCommandServiceImplTest {

  @Mock private CouponJpaRepository couponJpaRepository;

  @Mock private CouponCodeGenerator couponCodeGenerator;

  @InjectMocks private CouponCommandServiceImpl couponCommandService;

  private CreateCouponRequest createCommand;
  private DeleteCouponRequest deleteCommand;
  private Coupon coupon;

  @BeforeEach
  void setUp() {
    createCommand =
        CreateCouponRequest.builder()
            .couponTitle("테스트 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .secondaryItemId(200L)
            .discountRate(10)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(true)
            .build();

    deleteCommand = DeleteCouponRequest.builder().id(1L).build();

    coupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("테스트 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .secondaryItemId(200L)
            .discountRate(10)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("쿠폰 생성 성공 - 자동 코드 생성")
  void createCoupon_자동코드생성_성공() {
    // Given
    String generatedCode = "CP241201ABCD1234";
    given(couponCodeGenerator.generateCouponCode()).willReturn(generatedCode);
    given(couponJpaRepository.existsByCouponCodeAndNotDeleted(generatedCode)).willReturn(false);
    given(couponJpaRepository.save(any(Coupon.class))).willReturn(coupon);

    // When
    CouponDto result = couponCommandService.createCoupon(createCommand);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getCouponCode()).isEqualTo(generatedCode);
    assertThat(result.getCouponTitle()).isEqualTo("테스트 쿠폰");

    then(couponCodeGenerator).should(times(1)).generateCouponCode();
    then(couponJpaRepository).should(times(1)).existsByCouponCodeAndNotDeleted(generatedCode);
    then(couponJpaRepository).should(times(1)).save(any(Coupon.class));
  }

  @Test
  @DisplayName("쿠폰 생성 성공 - 자동 생성 중복 시 재시도")
  void createCoupon_자동생성중복재시도_성공() {
    // Given
    String firstCode = "CP241201ABCD1111";
    String secondCode = "CP241201ABCD2222";

    given(couponCodeGenerator.generateCouponCode()).willReturn(firstCode).willReturn(secondCode);
    given(couponJpaRepository.existsByCouponCodeAndNotDeleted(firstCode)).willReturn(true);
    given(couponJpaRepository.existsByCouponCodeAndNotDeleted(secondCode)).willReturn(false);
    given(couponJpaRepository.save(any(Coupon.class))).willReturn(coupon);

    // When
    CouponDto result = couponCommandService.createCoupon(createCommand);

    // Then
    assertThat(result).isNotNull();

    then(couponCodeGenerator).should(times(2)).generateCouponCode();
    then(couponJpaRepository).should(times(1)).existsByCouponCodeAndNotDeleted(firstCode);
    then(couponJpaRepository).should(times(1)).existsByCouponCodeAndNotDeleted(secondCode);
    then(couponJpaRepository).should(times(1)).save(any(Coupon.class));
  }

  @Test
  @DisplayName("쿠폰 삭제 성공")
  void deleteCoupon_성공() {
    // Given
    given(
            couponJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(
                deleteCommand.getId(), deleteCommand.getShopId()))
        .willReturn(Optional.of(coupon));
    given(couponJpaRepository.save(any(Coupon.class))).willReturn(coupon);

    // When
    couponCommandService.deleteCoupon(deleteCommand);

    // Then
    then(couponJpaRepository).should(times(1)).findByIdAndShopIdAndDeletedAtIsNull(1L, null);
    then(couponJpaRepository).should(times(1)).save(any(Coupon.class));
  }

  @Test
  @DisplayName("쿠폰 삭제 실패 - 존재하지 않는 쿠폰")
  void deleteCoupon_존재하지않는쿠폰_실패() {
    // Given
    given(
            couponJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(
                deleteCommand.getId(), deleteCommand.getShopId()))
        .willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> couponCommandService.deleteCoupon(deleteCommand))
        .isInstanceOf(BusinessException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_NOT_FOUND);

    then(couponJpaRepository).should(never()).save(any(Coupon.class));
  }

  @Test
  @DisplayName("쿠폰 삭제 실패 - 이미 삭제된 쿠폰")
  void deleteCoupon_이미삭제된쿠폰_실패() {
    // Given
    Coupon deletedCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("삭제된 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .discountRate(10)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(false)
            .createdAt(LocalDateTime.now())
            .deletedAt(LocalDateTime.now())
            .build();

    given(
            couponJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(
                deleteCommand.getId(), deleteCommand.getShopId()))
        .willReturn(Optional.of(deletedCoupon));

    // When & Then
    assertThatThrownBy(() -> couponCommandService.deleteCoupon(deleteCommand))
        .isInstanceOf(BusinessException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_ALREADY_DELETED);

    then(couponJpaRepository).should(never()).save(any(Coupon.class));
  }

  @Test
  @DisplayName("쿠폰 상태 토글 성공 - 활성화에서 비활성화")
  void toggleCouponStatus_활성화에서비활성화_성공() {
    // Given
    Coupon activeCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("활성 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .discountRate(10)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    given(couponJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(any(Long.class), any(Long.class)))
        .willReturn(Optional.of(activeCoupon));
    given(couponJpaRepository.save(any(Coupon.class))).willReturn(activeCoupon);

    // When
    CouponDto result = couponCommandService.toggleCouponStatus(1L, 1L);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getIsActive()).isFalse(); // 비활성화됨

    then(couponJpaRepository).should(times(1)).findByIdAndShopIdAndDeletedAtIsNull(1L, 1L);
    then(couponJpaRepository).should(times(1)).save(any(Coupon.class));
  }

  @Test
  @DisplayName("쿠폰 상태 토글 성공 - 비활성화에서 활성화")
  void toggleCouponStatus_비활성화에서활성화_성공() {
    // Given
    Coupon inactiveCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("비활성 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .discountRate(10)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(false)
            .createdAt(LocalDateTime.now())
            .build();

    given(couponJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(any(Long.class), any(Long.class)))
        .willReturn(Optional.of(inactiveCoupon));
    given(couponJpaRepository.save(any(Coupon.class))).willReturn(inactiveCoupon);

    // When
    CouponDto result = couponCommandService.toggleCouponStatus(1L, 1L);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getIsActive()).isTrue(); // 활성화됨

    then(couponJpaRepository).should(times(1)).findByIdAndShopIdAndDeletedAtIsNull(1L, 1L);
    then(couponJpaRepository).should(times(1)).save(any(Coupon.class));
  }

  @Test
  @DisplayName("쿠폰 상태 토글 실패 - 존재하지 않는 쿠폰")
  void toggleCouponStatus_존재하지않는쿠폰_실패() {
    // Given
    given(couponJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(any(Long.class), any(Long.class)))
        .willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> couponCommandService.toggleCouponStatus(1L, 1L))
        .isInstanceOf(BusinessException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_NOT_FOUND);

    then(couponJpaRepository).should(never()).save(any(Coupon.class));
  }

  @Test
  @DisplayName("쿠폰 상태 토글 실패 - 삭제된 쿠폰")
  void toggleCouponStatus_삭제된쿠폰_실패() {
    // Given
    Coupon deletedCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("삭제된 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .discountRate(10)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(false)
            .createdAt(LocalDateTime.now())
            .deletedAt(LocalDateTime.now())
            .build();

    given(couponJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(any(Long.class), any(Long.class)))
        .willReturn(Optional.of(deletedCoupon));

    // When & Then
    assertThatThrownBy(() -> couponCommandService.toggleCouponStatus(1L, 1L))
        .isInstanceOf(BusinessException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DELETED_COUPON_OPERATION_NOT_ALLOWED);

    then(couponJpaRepository).should(never()).save(any(Coupon.class));
  }
}
