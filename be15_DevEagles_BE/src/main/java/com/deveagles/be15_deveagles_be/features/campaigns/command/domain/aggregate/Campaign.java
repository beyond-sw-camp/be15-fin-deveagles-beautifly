package com.deveagles.be15_deveagles_be.features.campaigns.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "campaign")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Campaign {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "campaign_id")
  private Long id;

  @Column(name = "campaign_title", nullable = false, length = 50)
  private String campaignTitle;

  @Column(name = "description", nullable = false, length = 255)
  private String description;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  @Column(name = "message_send_at", nullable = false)
  private LocalDateTime messageSendAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "staff_id", nullable = false)
  private Long staffId;

  @Column(name = "template_id", nullable = false)
  private Long templateId;

  @Column(name = "coupon_id", nullable = false)
  private Long couponId;

  @Column(name = "customer_grade_id", nullable = false)
  private Long customerGradeId;

  @Column(name = "tag_id", nullable = false)
  private Long tagId;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  // Business methods
  public void softDelete() {
    this.deletedAt = LocalDateTime.now();
  }

  public boolean isDeleted() {
    return this.deletedAt != null;
  }

  public boolean isActive() {
    LocalDate now = LocalDate.now();
    return !isDeleted()
        && (startDate.isBefore(now) || startDate.isEqual(now))
        && (endDate.isAfter(now) || endDate.isEqual(now));
  }

  public boolean canBeSent() {
    return isActive() && messageSendAt != null && messageSendAt.isBefore(LocalDateTime.now());
  }
}
