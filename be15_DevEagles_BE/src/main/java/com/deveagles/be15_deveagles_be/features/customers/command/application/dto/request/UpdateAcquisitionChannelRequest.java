package com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAcquisitionChannelRequest {

  @NotBlank(message = "채널명은 필수입니다.")
  @Size(max = 20, message = "채널명은 20자 이하여야 합니다.")
  private String channelName;
}
