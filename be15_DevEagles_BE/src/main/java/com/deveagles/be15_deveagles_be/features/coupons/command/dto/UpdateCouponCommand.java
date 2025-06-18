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
public class UpdateCouponCommand {

  @NotNull(message = "쿠폰 ID는 필수입니다") private Long id;

  @Size(max = 50, message = "쿠폰명은 50자 이하여야 합니다")
  private String couponTitle;

  private Long staffId;

  private Long primaryItemId;

  private Long secondaryItemId;

  @Min(value = 0, message = "할인율은 0% 이상이어야 합니다")
  @Max(value = 100, message = "할인율은 100% 이하여야 합니다")
  private Integer discountRate;

  private LocalDate expirationDate;

  private Boolean isActive;
}
