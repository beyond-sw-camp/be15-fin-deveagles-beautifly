package com.deveagles.be15_deveagles_be.features.workflows.execution.application.service;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.WorkflowExecution;
import com.deveagles.be15_deveagles_be.features.workflows.execution.application.service.WorkflowExecutionService.ActionExecutionResult;
import java.util.List;

public interface ActionExecutorService {

  ActionExecutionResult executeAction(
      Workflow workflow, List<Long> targetCustomerIds, WorkflowExecution execution);
}
