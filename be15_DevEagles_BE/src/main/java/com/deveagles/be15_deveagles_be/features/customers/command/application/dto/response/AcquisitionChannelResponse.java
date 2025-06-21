package com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcquisitionChannelResponse {

  private Long id;
  private String channelName;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
}
