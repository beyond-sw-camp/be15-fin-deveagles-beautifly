package com.deveagles.be15_deveagles_be.features.coupons.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.domain.vo.DiscountResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CouponDiscountCalculator 단위 테스트")
class CouponDiscountCalculatorTest {

  private CouponDiscountCalculator couponDiscountCalculator;
  private Coupon coupon;

  @BeforeEach
  void setUp() {
    // 순수 도메인 서비스는 DI 없이 직접 생성
    couponDiscountCalculator = new CouponDiscountCalculator();

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

    // When
    DiscountResult result = couponDiscountCalculator.calculateDiscount(coupon, originalAmount);

    // Then
    assertThat(result.getOriginalAmount()).isEqualTo(10000);
    assertThat(result.getDiscountRate()).isEqualTo(10);
    assertThat(result.getDiscountAmount()).isEqualTo(1000);
    assertThat(result.getFinalAmount()).isEqualTo(9000);
    assertThat(result.isValid()).isTrue();
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

    // When
    DiscountResult result =
        couponDiscountCalculator.calculateDiscount(highDiscountCoupon, originalAmount);

    // Then
    assertThat(result.getOriginalAmount()).isEqualTo(5000);
    assertThat(result.getDiscountRate()).isEqualTo(20);
    assertThat(result.getDiscountAmount()).isEqualTo(1000);
    assertThat(result.getFinalAmount()).isEqualTo(4000);
    assertThat(result.isValid()).isTrue();
  }

  @Test
  @DisplayName("할인 계산 실패 - 유효하지 않은 금액 (null)")
  void calculateDiscount_null금액_실패() {
    // Given
    Integer originalAmount = null;

    // When & Then
    assertThatThrownBy(() -> couponDiscountCalculator.calculateDiscount(coupon, originalAmount))
        .isInstanceOf(BusinessException.class)
        .extracting(ex -> ((BusinessException) ex).getOriginalMessage())
        .isEqualTo("유효하지 않은 금액입니다");
  }

  @Test
  @DisplayName("할인 계산 실패 - 유효하지 않은 금액 (0 이하)")
  void calculateDiscount_0이하금액_실패() {
    // Given
    Integer originalAmount = 0;

    // When & Then
    assertThatThrownBy(() -> couponDiscountCalculator.calculateDiscount(coupon, originalAmount))
        .isInstanceOf(BusinessException.class)
        .extracting(ex -> ((BusinessException) ex).getOriginalMessage())
        .isEqualTo("유효하지 않은 금액입니다");
  }

  @Test
  @DisplayName("할인 계산 실패 - null 쿠폰")
  void calculateDiscount_null쿠폰_실패() {
    // Given
    Coupon nullCoupon = null;
    Integer originalAmount = 10000;

    // When & Then
    assertThatThrownBy(() -> couponDiscountCalculator.calculateDiscount(nullCoupon, originalAmount))
        .isInstanceOf(BusinessException.class)
        .extracting(ex -> ((BusinessException) ex).getOriginalMessage())
        .isEqualTo("쿠폰이 null입니다");
  }

  @Test
  @DisplayName("할인 계산 실패 - 유효하지 않은 할인율")
  void calculateDiscount_유효하지않은할인율_실패() {
    // Given
    Coupon invalidCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("잘못된 쿠폰")
            .shopId(1L)
            .primaryItemId(100L)
            .discountRate(150) // 100% 초과
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    Integer originalAmount = 10000;

    // When & Then
    assertThatThrownBy(
            () -> couponDiscountCalculator.calculateDiscount(invalidCoupon, originalAmount))
        .isInstanceOf(BusinessException.class)
        .extracting(ex -> ((BusinessException) ex).getOriginalMessage())
        .isEqualTo("유효하지 않은 할인율입니다");
  }

  @Test
  @DisplayName("할인 계산 성공 - 소수점 버림")
  void calculateDiscount_소수점버림_성공() {
    // Given
    Integer originalAmount = 999; // 10% 할인 시 99.9원 -> 99원

    // When
    DiscountResult result = couponDiscountCalculator.calculateDiscount(coupon, originalAmount);

    // Then
    assertThat(result.getOriginalAmount()).isEqualTo(999);
    assertThat(result.getDiscountRate()).isEqualTo(10);
    assertThat(result.getDiscountAmount()).isEqualTo(99); // 소수점 버림
    assertThat(result.getFinalAmount()).isEqualTo(900);
    assertThat(result.isValid()).isTrue();
  }

  @Test
  @DisplayName("할인 계산 성공 - 0% 할인")
  void calculateDiscount_0퍼센트할인_성공() {
    // Given
    Coupon noDiscountCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("무료 쿠폰")
            .shopId(1L)
            .primaryItemId(100L)
            .discountRate(0)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    Integer originalAmount = 10000;

    // When
    DiscountResult result =
        couponDiscountCalculator.calculateDiscount(noDiscountCoupon, originalAmount);

    // Then
    assertThat(result.getOriginalAmount()).isEqualTo(10000);
    assertThat(result.getDiscountRate()).isEqualTo(0);
    assertThat(result.getDiscountAmount()).isEqualTo(0);
    assertThat(result.getFinalAmount()).isEqualTo(10000);
    assertThat(result.isValid()).isTrue();
  }

  @Test
  @DisplayName("할인 계산 성공 - 100% 할인")
  void calculateDiscount_100퍼센트할인_성공() {
    // Given
    Coupon fullDiscountCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("무료 쿠폰")
            .shopId(1L)
            .primaryItemId(100L)
            .discountRate(100)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    Integer originalAmount = 10000;

    // When
    DiscountResult result =
        couponDiscountCalculator.calculateDiscount(fullDiscountCoupon, originalAmount);

    // Then
    assertThat(result.getOriginalAmount()).isEqualTo(10000);
    assertThat(result.getDiscountRate()).isEqualTo(100);
    assertThat(result.getDiscountAmount()).isEqualTo(10000);
    assertThat(result.getFinalAmount()).isEqualTo(0);
    assertThat(result.isValid()).isTrue();
  }
}
