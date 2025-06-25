package com.deveagles.be15_deveagles_be.features.workflows.execution.application.service;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;

public interface WorkflowExecutionService {

  void executeWorkflow(Workflow workflow);

  void executeTriggeredWorkflow(Workflow workflow, Long customerId);

  class ActionExecutionResult {
    private final int successCount;
    private final int failureCount;

    public ActionExecutionResult(int successCount, int failureCount) {
      this.successCount = successCount;
      this.failureCount = failureCount;
    }

    public int getSuccessCount() {
      return successCount;
    }

    public int getFailureCount() {
      return failureCount;
    }
  }
}
