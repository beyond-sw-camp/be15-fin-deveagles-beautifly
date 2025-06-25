package com.deveagles.be15_deveagles_be.features.workflows.command.domain.vo;

import lombok.Getter;

@Getter
public enum ExecutionStatus {
  SCHEDULED("SCHEDULED", "예약됨", "실행 예약된 상태"),
  RUNNING("RUNNING", "실행 중", "현재 실행 중인 상태"),
  SUCCESS("SUCCESS", "성공", "성공적으로 완료된 상태"),
  FAILED("FAILED", "실패", "실행 중 오류가 발생한 상태"),
  CANCELLED("CANCELLED", "취소됨", "실행이 취소된 상태");

  private final String code;
  private final String displayName;
  private final String description;

  ExecutionStatus(String code, String displayName, String description) {
    this.code = code;
    this.displayName = displayName;
    this.description = description;
  }

  public static ExecutionStatus fromCode(String code) {
    for (ExecutionStatus status : values()) {
      if (status.code.equals(code)) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown execution status code: " + code);
  }

  public boolean isCompleted() {
    return this == SUCCESS || this == FAILED || this == CANCELLED;
  }

  public boolean isInProgress() {
    return this == SCHEDULED || this == RUNNING;
  }

  public boolean isSuccessful() {
    return this == SUCCESS;
  }
}
