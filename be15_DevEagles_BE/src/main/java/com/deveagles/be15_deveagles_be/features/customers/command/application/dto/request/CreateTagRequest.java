package com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTagRequest {

  @NotBlank(message = "태그명은 필수입니다.")
  @Size(max = 10, message = "태그명은 10자 이하여야 합니다.")
  private String tagName;

  @NotBlank(message = "색상코드는 필수입니다.")
  @Size(max = 20, message = "색상코드는 20자 이하여야 합니다.")
  private String colorCode;
}
