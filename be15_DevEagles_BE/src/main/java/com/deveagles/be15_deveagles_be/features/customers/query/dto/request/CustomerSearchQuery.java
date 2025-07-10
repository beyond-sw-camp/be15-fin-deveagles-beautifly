package com.deveagles.be15_deveagles_be.features.customers.query.dto.request;

import java.util.List;

public record CustomerSearchQuery(
    Long shopId,
    String keyword, // 이름 또는 전화번호로 검색
    List<Long> customerGradeIds, // 여러 등급 지원
    List<String> tagIds, // 태그 지원
    String gender, // M 또는 F
    Boolean marketingConsent,
    Boolean notificationConsent,
    Boolean excludeDormant, // 휴면 고객 제외
    Integer dormantMonths, // 휴면 기간(개월)
    Boolean excludeRecentMessage, // 최근 메시지 수신자 제외
    Integer recentMessageDays, // 최근 메시지 기간(일)
    Boolean includeDeleted,
    int page,
    int size,
    String sortBy,
    String sortDirection) {
  public CustomerSearchQuery {
    if (page < 0) page = 0;
    if (size <= 0) size = 20;
    if (size > 100) size = 100;
    if (sortBy == null) sortBy = "createdAt";
    if (sortDirection == null) sortDirection = "DESC";
    if (includeDeleted == null) includeDeleted = false;
    if (excludeDormant == null) excludeDormant = false;
    if (excludeRecentMessage == null) excludeRecentMessage = false;
  }
}
