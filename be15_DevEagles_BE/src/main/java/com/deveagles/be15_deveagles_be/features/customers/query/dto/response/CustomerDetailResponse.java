package com.deveagles.be15_deveagles_be.features.customers.query.dto.response;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer.Gender;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDetailResponse {

  private Long customerId;
  private String customerName;
  private String phoneNumber;
  private String memo;
  private Integer visitCount;
  private Integer totalRevenue;
  private LocalDate recentVisitDate;
  private LocalDate birthdate;
  private Integer noshowCount;
  private Gender gender;
  private Boolean marketingConsent;
  private LocalDateTime marketingConsentedAt;
  private Boolean notificationConsent;
  private LocalDateTime lastMessageSentAt;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;
  private Long shopId;
  private StaffInfo staff;
  private CustomerGradeInfo customerGrade;
  private AcquisitionChannelInfo acquisitionChannel;
  private Integer remainingPrepaidAmount;
  private List<TagInfo> tags = new ArrayList<>();

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class StaffInfo {
    private Long staffId;
    private String staffName;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class CustomerGradeInfo {
    private Long customerGradeId;
    private String customerGradeName;
    private Integer discountRate;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class AcquisitionChannelInfo {
    private Long acquisitionChannelId;
    private String acquisitionChannelName;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class TagInfo {
    private Long tagId;
    private String tagName;
    private String colorCode;
  }

  public CustomerDetailResponse(
      Long customerId,
      String customerName,
      String phoneNumber,
      String memo,
      Integer visitCount,
      Integer totalRevenue,
      LocalDate recentVisitDate,
      LocalDate birthdate,
      Integer noshowCount,
      Gender gender,
      Boolean marketingConsent,
      LocalDateTime marketingConsentedAt,
      Boolean notificationConsent,
      LocalDateTime lastMessageSentAt,
      LocalDateTime createdAt,
      LocalDateTime modifiedAt,
      Long shopId,
      Long staffId,
      String staffName,
      Long customerGradeId,
      String customerGradeName,
      Integer discountRate,
      Long acquisitionChannelId,
      String acquisitionChannelName,
      Integer remainingPrepaidAmount) {
    this.customerId = customerId;
    this.customerName = customerName;
    this.phoneNumber = phoneNumber;
    this.memo = memo;
    this.visitCount = visitCount;
    this.totalRevenue = totalRevenue;
    this.recentVisitDate = recentVisitDate;
    this.birthdate = birthdate;
    this.noshowCount = noshowCount;
    this.gender = gender;
    this.marketingConsent = marketingConsent;
    this.marketingConsentedAt = marketingConsentedAt;
    this.notificationConsent = notificationConsent;
    this.lastMessageSentAt = lastMessageSentAt;
    this.createdAt = createdAt;
    this.modifiedAt = modifiedAt;
    this.shopId = shopId;
    this.staff = StaffInfo.builder().staffId(staffId).staffName(staffName).build();
    this.customerGrade =
        CustomerGradeInfo.builder()
            .customerGradeId(customerGradeId)
            .customerGradeName(customerGradeName)
            .discountRate(discountRate)
            .build();
    this.acquisitionChannel =
        AcquisitionChannelInfo.builder()
            .acquisitionChannelId(acquisitionChannelId)
            .acquisitionChannelName(acquisitionChannelName)
            .build();
    this.remainingPrepaidAmount = remainingPrepaidAmount;
  }
}
