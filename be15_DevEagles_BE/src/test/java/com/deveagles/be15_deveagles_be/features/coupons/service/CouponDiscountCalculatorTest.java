package com.deveagles.be15_deveagles_be.features.coupons.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.deveagles.be15_deveagles_be.features.coupons.common.CouponResponseFactory;
import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.domain.service.CouponDiscountCalculator;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponApplicationResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponDiscountCalculator 단위 테스트")
class CouponDiscountCalculatorTest {

  @Mock private CouponResponseFactory couponResponseFactory;

  private CouponDiscountCalculator couponDiscountCalculator;
  private Coupon coupon;

  @BeforeEach
  void setUp() {
    couponDiscountCalculator = new CouponDiscountCalculator(couponResponseFactory);

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
  @DisplayName("할인 계산 성공 - 10% 할인")
  void calculateDiscount_10퍼센트할인_성공() {
    // Given
    Integer originalAmount = 10000;
    CouponApplicationResponse expectedResponse =
        new CouponApplicationResponse(true, null, coupon, 10, 1000, 9000);
    given(couponResponseFactory.createSuccessResponse(coupon, 10, 1000, 9000))
        .willReturn(expectedResponse);

    // When
    CouponApplicationResponse result =
        couponDiscountCalculator.calculateDiscount(coupon, originalAmount);

    // Then
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getDiscountRate()).isEqualTo(10);
    assertThat(result.getDiscountAmount()).isEqualTo(1000);
    assertThat(result.getFinalAmount()).isEqualTo(9000);
  }

  @Test
  @DisplayName("할인 계산 성공 - 20% 할인")
  void calculateDiscount_20퍼센트할인_성공() {
    // Given
    Coupon highDiscountCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("고할인 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .discountRate(20)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    Integer originalAmount = 5000;
    CouponApplicationResponse expectedResponse =
        new CouponApplicationResponse(true, null, highDiscountCoupon, 20, 1000, 4000);
    given(couponResponseFactory.createSuccessResponse(highDiscountCoupon, 20, 1000, 4000))
        .willReturn(expectedResponse);

    // When
    CouponApplicationResponse result =
        couponDiscountCalculator.calculateDiscount(highDiscountCoupon, originalAmount);

    // Then
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getDiscountRate()).isEqualTo(20);
    assertThat(result.getDiscountAmount()).isEqualTo(1000);
    assertThat(result.getFinalAmount()).isEqualTo(4000);
  }

  @Test
  @DisplayName("할인 계산 실패 - 유효하지 않은 금액 (null)")
  void calculateDiscount_null금액_실패() {
    // Given
    Integer originalAmount = null;
    CouponApplicationResponse expectedResponse =
        new CouponApplicationResponse(false, "유효하지 않은 금액입니다", null, null, null, null);
    given(couponResponseFactory.createFailedResponse("유효하지 않은 금액입니다")).willReturn(expectedResponse);

    // When
    CouponApplicationResponse result =
        couponDiscountCalculator.calculateDiscount(coupon, originalAmount);

    // Then
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getErrorMessage()).isEqualTo("유효하지 않은 금액입니다");
  }

  @Test
  @DisplayName("할인 계산 실패 - 유효하지 않은 금액 (0 이하)")
  void calculateDiscount_0이하금액_실패() {
    // Given
    Integer originalAmount = 0;
    CouponApplicationResponse expectedResponse =
        new CouponApplicationResponse(false, "유효하지 않은 금액입니다", null, null, null, null);
    given(couponResponseFactory.createFailedResponse("유효하지 않은 금액입니다")).willReturn(expectedResponse);

    // When
    CouponApplicationResponse result =
        couponDiscountCalculator.calculateDiscount(coupon, originalAmount);

    // Then
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getErrorMessage()).isEqualTo("유효하지 않은 금액입니다");
  }

  @Test
  @DisplayName("할인 계산 성공 - 소수점 버림")
  void calculateDiscount_소수점버림_성공() {
    // Given
    Integer originalAmount = 999; // 10% 할인 시 99.9원 -> 99원
    CouponApplicationResponse expectedResponse =
        new CouponApplicationResponse(true, null, coupon, 10, 99, 900);
    given(couponResponseFactory.createSuccessResponse(coupon, 10, 99, 900))
        .willReturn(expectedResponse);

    // When
    CouponApplicationResponse result =
        couponDiscountCalculator.calculateDiscount(coupon, originalAmount);

    // Then
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getDiscountRate()).isEqualTo(10);
    assertThat(result.getDiscountAmount()).isEqualTo(99); // 소수점 버림
    assertThat(result.getFinalAmount()).isEqualTo(900);
  }
}
