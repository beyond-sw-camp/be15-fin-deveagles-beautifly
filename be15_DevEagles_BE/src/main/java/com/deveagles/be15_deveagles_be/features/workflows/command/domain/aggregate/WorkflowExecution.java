package com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "workflow_execution")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WorkflowExecution {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "execution_id")
  private Long id;

  @Column(name = "workflow_id", nullable = false)
  private Long workflowId;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "execution_status", nullable = false, length = 20)
  private String executionStatus; // SCHEDULED, RUNNING, SUCCESS, FAILED, CANCELLED

  @Column(name = "trigger_type", nullable = false, length = 50)
  private String triggerType;

  @Column(name = "action_type", nullable = false, length = 50)
  private String actionType;

  @Column(name = "target_count")
  private Integer targetCount; // 대상 고객 수

  @Column(name = "success_count")
  @Builder.Default
  private Integer successCount = 0;

  @Column(name = "failure_count")
  @Builder.Default
  private Integer failureCount = 0;

  @Column(name = "error_message", length = 1000)
  private String errorMessage;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "execution_details", columnDefinition = "json")
  private String executionDetails; // 실행 세부 정보 JSON

  @Column(name = "started_at")
  private LocalDateTime startedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // Business methods
  public void start() {
    this.executionStatus = "RUNNING";
    this.startedAt = LocalDateTime.now();
  }

  public void complete() {
    this.executionStatus = "SUCCESS";
    this.completedAt = LocalDateTime.now();
  }

  public void fail(String errorMessage) {
    this.executionStatus = "FAILED";
    this.errorMessage = errorMessage;
    this.completedAt = LocalDateTime.now();
  }

  public void cancel() {
    this.executionStatus = "CANCELLED";
    this.completedAt = LocalDateTime.now();
  }

  public void updateCounts(Integer successCount, Integer failureCount) {
    this.successCount = successCount;
    this.failureCount = failureCount;
  }

  public void setTargetCount(Integer targetCount) {
    this.targetCount = targetCount;
  }

  public boolean isCompleted() {
    return "SUCCESS".equals(executionStatus)
        || "FAILED".equals(executionStatus)
        || "CANCELLED".equals(executionStatus);
  }

  public boolean isSuccessful() {
    return "SUCCESS".equals(executionStatus);
  }

  public double getSuccessRate() {
    int total =
        (successCount != null ? successCount : 0) + (failureCount != null ? failureCount : 0);
    if (total == 0) {
      return 0.0;
    }
    return (double) (successCount != null ? successCount : 0) / total * 100;
  }

  public long getDurationInSeconds() {
    if (startedAt == null || completedAt == null) {
      return 0;
    }
    return java.time.Duration.between(startedAt, completedAt).getSeconds();
  }
}
