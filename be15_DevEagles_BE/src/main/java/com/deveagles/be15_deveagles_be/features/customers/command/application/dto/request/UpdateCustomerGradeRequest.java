package com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerGradeRequest {

  @NotBlank(message = "고객등급명은 필수입니다.")
  @Size(max = 10, message = "고객등급명은 10자 이하여야 합니다.")
  private String customerGradeName;

  @NotNull(message = "할인율은 필수입니다.") @Min(value = 0, message = "할인율은 0% 이상이어야 합니다.")
  @Max(value = 100, message = "할인율은 100% 이하여야 합니다.")
  private Integer discountRate;
}
