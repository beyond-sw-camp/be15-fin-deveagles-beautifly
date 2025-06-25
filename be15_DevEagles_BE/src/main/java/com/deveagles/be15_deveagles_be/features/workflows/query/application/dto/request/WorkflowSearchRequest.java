package com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowSearchRequest {

  private Long shopId;
  private String searchQuery;
  private String statusFilter; // active, inactive, all
  private String triggerCategory; // lifecycle, periodic, special, prevention
  private String triggerType;
  private String actionType;
  private Boolean isActive;
  private int page;
  private int size;
  private String sortBy; // title, createdAt, modifiedAt, executionCount
  private String sortDirection; // asc, desc

  public WorkflowSearchRequest(Long shopId) {
    this.shopId = shopId;
    this.page = 0;
    this.size = 20;
    this.sortBy = "createdAt";
    this.sortDirection = "desc";
  }

  public boolean hasSearchQuery() {
    return searchQuery != null && !searchQuery.trim().isEmpty();
  }

  public boolean hasStatusFilter() {
    return statusFilter != null && !statusFilter.trim().isEmpty() && !"all".equals(statusFilter);
  }

  public boolean hasTriggerCategoryFilter() {
    return triggerCategory != null && !triggerCategory.trim().isEmpty();
  }

  public boolean hasTriggerTypeFilter() {
    return triggerType != null && !triggerType.trim().isEmpty();
  }

  public boolean hasActionTypeFilter() {
    return actionType != null && !actionType.trim().isEmpty();
  }

  public boolean hasActiveFilter() {
    return isActive != null;
  }
}
