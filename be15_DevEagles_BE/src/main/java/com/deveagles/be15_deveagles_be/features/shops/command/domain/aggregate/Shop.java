package com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Builder
@Getter
@Entity
@Table(name = "shop")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Shop {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "shop_name", nullable = false)
  private String shopName;

  @Column(name = "owner_id")
  private Long ownerId;

  @Column(name = "address", nullable = false)
  private String address;

  @Column(name = "detail_address", nullable = false)
  private String detailAddress;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "business_number")
  private String businessNumber;

  @Column(name = "industry_id", nullable = false)
  private Long industryId;

  @Column(name = "incentive_status", nullable = false)
  private boolean incentiveStatus = false;

  @Column(name = "reservation_term", nullable = false)
  private int reservationTerm = 30;

  @Column(name = "shop_description")
  private String shopDescription;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt;

  @Builder
  public Shop(
      String shopName,
      String address,
      String detailAddress,
      Long industryId,
      String phoneNumber,
      String businessNumber,
      String shopDescription) {
    this.shopName = shopName;
    this.address = address;
    this.detailAddress = detailAddress;
    this.industryId = industryId;
    this.phoneNumber = phoneNumber;
    this.businessNumber = businessNumber;
    this.shopDescription = shopDescription;
  }

  public void setIncentive(boolean status) {
    this.incentiveStatus = status;
  }

  public void setReservation(int term) {
    this.reservationTerm = term;
  }

  public void setOwner(Long ownerId) {
    this.ownerId = ownerId;
  }

  public void setName(String shopName) {
    this.shopName = shopName;
  }

  public void setShopAddress(String address) {
    this.address = address;
  }

  public void setShopDetailAddress(String detailAddress) {
    this.detailAddress = detailAddress;
  }

  public void setIndustry(Long industryId) {
    this.industryId = industryId;
  }

  public void setPhone(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setBizNumber(String businessNumber) {
    this.businessNumber = businessNumber;
  }
}
