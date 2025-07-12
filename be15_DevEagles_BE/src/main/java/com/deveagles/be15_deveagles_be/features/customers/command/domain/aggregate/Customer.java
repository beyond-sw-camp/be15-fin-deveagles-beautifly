package com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "customer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "customer_id")
  private Long id;

  @Column(name = "customer_grade_id", nullable = false)
  private Long customerGradeId;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "staff_id", nullable = false)
  private Long staffId;

  @Column(name = "customer_name", nullable = false, length = 100)
  private String customerName;

  @Column(name = "phone_number", nullable = false, length = 11)
  private String phoneNumber;

  @Column(name = "memo", length = 255)
  private String memo;

  @Builder.Default
  @ColumnDefault("0")
  @Column(name = "visit_count", nullable = false)
  private Integer visitCount = 0;

  @Builder.Default
  @ColumnDefault("0")
  @Column(name = "total_revenue", nullable = false)
  private Integer totalRevenue = 0;

  @Builder.Default
  @ColumnDefault("CURRENT_TIMESTAMP")
  @Column(name = "recent_visit_date", nullable = false)
  private LocalDate recentVisitDate = LocalDate.now();

  @Column(name = "birthdate", nullable = false)
  private LocalDate birthdate;

  @Builder.Default
  @ColumnDefault("0")
  @Column(name = "noshow_count", nullable = false)
  private Integer noshowCount = 0;

  @Enumerated(EnumType.STRING)
  @Column(name = "gender", length = 1)
  private Gender gender;

  @Builder.Default
  @Column(name = "marketing_consent", nullable = false)
  private Boolean marketingConsent = false;

  @Column(name = "marketing_consented_at")
  private LocalDateTime marketingConsentedAt;

  @Builder.Default
  @Column(name = "notification_consent", nullable = false)
  private Boolean notificationConsent = false;

  @Column(name = "last_message_sent_at")
  private LocalDateTime lastMessageSentAt;

  @Column(name = "channel_id")
  private Long channelId;

  @Column(name = "created_at", nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(name = "modified_at", nullable = false)
  @UpdateTimestamp
  private LocalDateTime modifiedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  // Business methods
  public void updateCustomerInfo(
      String customerName, String phoneNumber, String memo, Gender gender, Long channelId) {
    this.customerName = customerName;
    this.phoneNumber = phoneNumber;
    this.memo = memo;
    this.gender = gender;
    this.channelId = channelId;
  }

  public void updateStaff(Long staffId) {
    this.staffId = staffId;
  }

  public void updateGrade(Long customerGradeId) {
    this.customerGradeId = customerGradeId;
  }

  public void updateBirthdate(LocalDate birthdate) {
    this.birthdate = birthdate;
  }

  public void updateMarketingConsent(Boolean consent) {
    this.marketingConsent = consent;
    if (consent) {
      this.marketingConsentedAt = LocalDateTime.now();
    }
  }

  public void updateNotificationConsent(Boolean consent) {
    this.notificationConsent = consent;
  }

  public void addVisit(Integer revenue) {
    this.visitCount++;
    this.totalRevenue += revenue;
    this.recentVisitDate = LocalDate.now();
  }

  public void addNoshow() {
    this.noshowCount++;
  }

  public void updateLastMessageSentAt() {
    this.lastMessageSentAt = LocalDateTime.now();
  }

  public void softDelete() {
    this.deletedAt = LocalDateTime.now();
  }

  public boolean isDeleted() {
    return this.deletedAt != null;
  }

  public enum Gender {
    M,
    F
  }

  public void incrementVisitCount() {
    this.visitCount += 1;
  }

  public void addRevenue(int amount) {
    this.totalRevenue += amount;
  }

  public void updateRecentVisitDate(LocalDate visitDate) {
    this.recentVisitDate = visitDate;
    this.modifiedAt = LocalDateTime.now();
  }

  public void subtractRevenue(int amount) {
    this.totalRevenue -= amount;
  }
}
