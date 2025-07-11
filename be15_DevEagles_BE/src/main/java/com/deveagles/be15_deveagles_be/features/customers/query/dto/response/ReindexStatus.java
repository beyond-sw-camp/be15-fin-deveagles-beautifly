package com.deveagles.be15_deveagles_be.features.customers.query.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReindexStatus {

  private String taskId;
  private String status; // RUNNING, COMPLETED, FAILED
  private Long totalCount;
  private Long processedCount;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String errorMessage;
  private Long shopId; // null이면 전체 매장

  public static ReindexStatus createRunning(String taskId, Long totalCount, Long shopId) {
    return ReindexStatus.builder()
        .taskId(taskId)
        .status("RUNNING")
        .totalCount(totalCount)
        .processedCount(0L)
        .startTime(LocalDateTime.now())
        .shopId(shopId)
        .build();
  }

  public static ReindexStatus createCompleted(
      String taskId, Long totalCount, Long processedCount, LocalDateTime startTime, Long shopId) {
    return ReindexStatus.builder()
        .taskId(taskId)
        .status("COMPLETED")
        .totalCount(totalCount)
        .processedCount(processedCount)
        .startTime(startTime)
        .endTime(LocalDateTime.now())
        .shopId(shopId)
        .build();
  }

  public static ReindexStatus createFailed(
      String taskId, String errorMessage, LocalDateTime startTime, Long shopId) {
    return ReindexStatus.builder()
        .taskId(taskId)
        .status("FAILED")
        .errorMessage(errorMessage)
        .startTime(startTime)
        .endTime(LocalDateTime.now())
        .shopId(shopId)
        .build();
  }
}
