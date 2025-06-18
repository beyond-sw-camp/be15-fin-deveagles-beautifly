package com.deveagles.be15_deveagles_be.features.coupons.command.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCouponCommand {

  @NotBlank(message = "쿠폰 코드는 필수입니다")
  @Size(max = 50, message = "쿠폰 코드는 50자 이하여야 합니다")
  private String couponCode;

  @NotBlank(message = "쿠폰명은 필수입니다")
  @Size(max = 50, message = "쿠폰명은 50자 이하여야 합니다")
  private String couponTitle;

  private Long staffId;

  @NotNull(message = "1차 상품 ID는 필수입니다") private Long primaryItemId;

  private Long secondaryItemId;

  @NotNull(message = "할인율은 필수입니다") @Min(value = 0, message = "할인율은 0% 이상이어야 합니다")
  @Max(value = 100, message = "할인율은 100% 이하여야 합니다")
  private Integer discountRate;

  private LocalDate expirationDate;

  @NotNull(message = "활성화 여부는 필수입니다") private Boolean isActive;
}
