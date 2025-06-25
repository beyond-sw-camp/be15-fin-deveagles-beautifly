package com.deveagles.be15_deveagles_be.features.workflows.execution.application.service;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import java.util.List;

public interface CustomerFilterService {

  List<Long> filterTargetCustomerIds(Workflow workflow);

  List<Long> filterByTriggerConditions(List<Long> customerIds, Workflow workflow);
}
