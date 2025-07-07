package com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum DayOfWeekEnum {
  MON,
  TUE,
  WED,
  THU,
  FRI,
  SAT,
  SUN;

  public static DayOfWeekEnum fromKorean(String korean) {
    return switch (korean) {
      case "월" -> MON;
      case "화" -> TUE;
      case "수" -> WED;
      case "목" -> THU;
      case "금" -> FRI;
      case "토" -> SAT;
      case "일" -> SUN;
      default -> throw new BusinessException(ErrorCode.INVALID_DAY_OF_WEEK);
    };
  }

  public String toKorean() {
    return switch (this) {
      case MON -> "월";
      case TUE -> "화";
      case WED -> "수";
      case THU -> "목";
      case FRI -> "금";
      case SAT -> "토";
      case SUN -> "일";
    };
  }

  public int toFullCalendarIndex() {
    return switch (this) {
      case SUN -> 0;
      case MON -> 1;
      case TUE -> 2;
      case WED -> 3;
      case THU -> 4;
      case FRI -> 5;
      case SAT -> 6;
    };
  }
}
