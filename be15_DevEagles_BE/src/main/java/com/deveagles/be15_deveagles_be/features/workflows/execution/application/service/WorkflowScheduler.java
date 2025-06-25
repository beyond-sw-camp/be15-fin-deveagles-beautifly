package com.deveagles.be15_deveagles_be.features.workflows.execution.application.service;

public interface WorkflowScheduler {

  void executeScheduledWorkflows();

  void checkDailyTriggers();

  void checkPeriodicTriggers();
}
