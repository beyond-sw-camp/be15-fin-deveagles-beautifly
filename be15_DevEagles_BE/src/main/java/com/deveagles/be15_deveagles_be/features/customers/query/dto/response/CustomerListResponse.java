package com.deveagles.be15_deveagles_be.features.customers.query.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CustomerListResponse {

  private Long customerId;
  private String customerName;
  private String phoneNumber;
  private String memo;
  private int visitCount;
  private int totalRevenue;
  private LocalDate recentVisitDate;
  private LocalDate birthdate;
  private String gender;
  private Integer remainingPrepaidAmount;
  private int noshowCount;
  private LocalDateTime createdAt;
  private final List<TagInfo> tags = new ArrayList<>();
  private StaffInfo staff;
  private CustomerGradeInfo customerGrade;
  private AcquisitionChannelInfo acquisitionChannel;

  public CustomerListResponse(
      Long customerId,
      String customerName,
      String phoneNumber,
      String memo,
      int visitCount,
      int totalRevenue,
      LocalDate recentVisitDate,
      LocalDate birthdate,
      String gender,
      Integer remainingPrepaidAmount,
      int noshowCount,
      LocalDateTime createdAt) {
    this.customerId = customerId;
    this.customerName = customerName;
    this.phoneNumber = phoneNumber;
    this.memo = memo;
    this.visitCount = visitCount;
    this.totalRevenue = totalRevenue;
    this.recentVisitDate = recentVisitDate;
    this.birthdate = birthdate;
    this.gender = gender;
    this.remainingPrepaidAmount = remainingPrepaidAmount;
    this.noshowCount = noshowCount;
    this.createdAt = createdAt;
  }

  public CustomerListResponse(
      Long customerId,
      String customerName,
      String phoneNumber,
      String memo,
      int visitCount,
      int totalRevenue,
      LocalDate recentVisitDate,
      LocalDate birthdate,
      String gender,
      Long customerGradeId,
      String customerGradeName,
      Integer discountRate,
      Long staffId,
      String staffName,
      Long acquisitionChannelId,
      String acquisitionChannelName,
      Integer remainingPrepaidAmount,
      int noshowCount,
      LocalDateTime createdAt) {
    this.customerId = customerId;
    this.customerName = customerName;
    this.phoneNumber = phoneNumber;
    this.memo = memo;
    this.visitCount = visitCount;
    this.totalRevenue = totalRevenue;
    this.recentVisitDate = recentVisitDate;
    this.birthdate = birthdate;
    this.gender = gender;
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
    this.noshowCount = noshowCount;
    this.createdAt = createdAt;
  }

  @Getter
  @NoArgsConstructor
  @Builder
  public static class TagInfo {
    private Long tagId;
    private String tagName;
    private String colorCode;

    public TagInfo(Long tagId, String tagName, String colorCode) {
      this.tagId = tagId;
      this.tagName = tagName;
      this.colorCode = colorCode;
    }
  }

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
}
