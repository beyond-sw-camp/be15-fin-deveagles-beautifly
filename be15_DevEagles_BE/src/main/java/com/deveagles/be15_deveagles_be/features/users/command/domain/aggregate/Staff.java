package com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate;

import jakarta.persistence.*;
import java.sql.Date;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Entity
@Builder
@Table(name = "staff")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Staff {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "staff_id", nullable = false)
  private Long staffId;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "login_id", nullable = false)
  private String loginId;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "staff_name", nullable = false)
  private String staffName;

  @Column(name = "grade", nullable = false)
  private String grade;

  @Column(name = "phone_number", nullable = false)
  private String phoneNumber;

  @Column(name = "email")
  private String email;

  @Column(name = "joined_date")
  private Date joinedDate;

  @Column(name = "left_date")
  private Date leftDate;

  @Column(name = "staff_description")
  private String staffDescription;

  @Column(name = "color_code", nullable = false)
  private String colorCode;

  @Column(name = "profile_url")
  private String profileUrl;

  @Enumerated(EnumType.STRING)
  @Column(name = "staff_status", nullable = false)
  private StaffStatus staffStatus;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt;

  @Builder
  public Staff(
      Long shopId,
      String loginId,
      String password,
      String staffName,
      String grade,
      String phoneNumber,
      String email) {
    this.shopId = shopId;
    this.loginId = loginId;
    this.password = password;
    this.staffName = staffName;
    this.grade = grade;
    this.phoneNumber = phoneNumber;
    this.email = email;
    this.colorCode = "#364f6b";
    this.staffStatus = StaffStatus.STAFF;
    this.createdAt = LocalDateTime.now();
    this.modifiedAt = LocalDateTime.now();
  }

  public void setEncodedPassword(String encodedPassword) {
    this.password = encodedPassword;
  }

  public void setOwner() {
    this.staffStatus = StaffStatus.OWNER;
  }

  public void setStaff(
      String profileUrl,
      String staffName,
      String phoneNumber,
      String grade,
      Date joinedDate,
      Date leftDate,
      String staffDescription,
      String colorCode) {
    this.profileUrl = profileUrl;
    this.staffName = staffName;
    this.phoneNumber = phoneNumber;
    this.grade = grade;
    this.joinedDate = joinedDate;
    this.leftDate = leftDate;
    this.staffDescription = staffDescription;
    this.colorCode = colorCode;
  }

  public void patchEmail(String email) {
    this.email = email;
  }

  public void patchPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void patchName(String staffName) {
    this.staffName = staffName;
  }

  public void patchGrade(String grade) {
    this.grade = grade;
  }

  public void patchProfileUrl(String profileUrl) {
    this.profileUrl = profileUrl;
  }

  public void patchColorCode(String colorCode) {
    this.colorCode = colorCode;
  }

  public void patchDescription(String staffDescription) {
    this.staffDescription = staffDescription;
  }

  public void patchJoinedDate(Date joinedDate) {
    this.joinedDate = joinedDate;
  }

  public void patchLeftDate(Date leftDate) {
    this.leftDate = leftDate;
  }
}
