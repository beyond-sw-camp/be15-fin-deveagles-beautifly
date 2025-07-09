package com.deveagles.be15_deveagles_be.features.campaigns.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampaignRequest {

  @NotBlank(message = "캠페인 제목은 필수입니다.")
  @Size(max = 50, message = "캠페인 제목은 50자 이하여야 합니다.")
  private String campaignTitle;

  @Size(max = 255, message = "설명은 255자 이하여야 합니다.")
  private String description;

  @NotNull(message = "시작일은 필수입니다.") private LocalDate startDate;

  @NotNull(message = "종료일은 필수입니다.") private LocalDate endDate;

  @NotNull(message = "메시지 발송 시간은 필수입니다.") private LocalDateTime messageSendAt;

  @NotNull(message = "작성자 ID는 필수입니다.") private Long staffId;

  @NotNull(message = "템플릿 ID는 필수입니다.") private Long templateId;

  @NotNull(message = "쿠폰 ID는 필수입니다.") private Long couponId;

  @NotNull(message = "고객 등급 ID는 필수입니다.") private Long customerGradeId;

  @NotNull(message = "태그 ID는 필수입니다.") private Long tagId;

  @NotNull(message = "상점 ID는 필수입니다.") private Long shopId;
}
