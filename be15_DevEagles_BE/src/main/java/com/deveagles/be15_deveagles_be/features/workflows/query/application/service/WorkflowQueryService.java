package com.deveagles.be15_deveagles_be.features.workflows.query.application.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.request.WorkflowSearchRequest;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowQueryResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowStatsResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowSummaryResponse;
import java.util.List;

public interface WorkflowQueryService {

  WorkflowQueryResponse getWorkflowById(Long workflowId, Long shopId);

  PagedResponse<WorkflowSummaryResponse> searchWorkflows(WorkflowSearchRequest request);

  WorkflowStatsResponse getWorkflowStats(Long shopId);

  List<WorkflowSummaryResponse> getWorkflowsByTriggerCategory(String triggerCategory, Long shopId);

  List<WorkflowSummaryResponse> getWorkflowsByTriggerType(String triggerType, Long shopId);

  List<WorkflowSummaryResponse> getRecentWorkflows(Long shopId, int limit);

  List<WorkflowSummaryResponse> getActiveWorkflows(Long shopId);
}
