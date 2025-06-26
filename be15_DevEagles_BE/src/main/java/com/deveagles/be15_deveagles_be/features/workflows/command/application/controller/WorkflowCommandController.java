package com.deveagles.be15_deveagles_be.features.workflows.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.workflows.command.application.dto.request.CreateWorkflowCommand;
import com.deveagles.be15_deveagles_be.features.workflows.command.application.dto.request.DeleteWorkflowCommand;
import com.deveagles.be15_deveagles_be.features.workflows.command.application.dto.request.UpdateWorkflowCommand;
import com.deveagles.be15_deveagles_be.features.workflows.command.application.service.WorkflowCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "워크플로우 관리", description = "워크플로우 생성, 수정, 삭제 API")
@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
@Validated
@Slf4j
public class WorkflowCommandController {

  private final WorkflowCommandService workflowCommandService;

  @Operation(summary = "워크플로우 생성", description = "새로운 워크플로우를 생성합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "워크플로우 생성 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "중복된 워크플로우 제목")
  })
  @PostMapping
  public ResponseEntity<ApiResponse<WorkflowCreateResponse>> createWorkflow(
      @Parameter(description = "워크플로우 생성 정보", required = true) @Valid @RequestBody
          CreateWorkflowCommand command) {
    log.info("워크플로우 생성 요청: 제목={}, 매장ID={}", command.getTitle(), command.getShopId());

    Long workflowId = workflowCommandService.createWorkflow(command);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(new WorkflowCreateResponse(workflowId)));
  }

  @Operation(summary = "워크플로우 수정", description = "기존 워크플로우의 정보를 수정합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "워크플로우 수정 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "워크플로우를 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "중복된 워크플로우 제목")
  })
  @PutMapping("/{workflowId}")
  public ResponseEntity<ApiResponse<Void>> updateWorkflow(
      @Parameter(description = "워크플로우 ID", required = true, example = "1") @PathVariable
          Long workflowId,
      @Parameter(description = "워크플로우 수정 정보", required = true) @Valid @RequestBody
          UpdateWorkflowCommand command) {
    log.info("워크플로우 수정 요청: ID={}, 제목={}", workflowId, command.getTitle());

    UpdateWorkflowCommand commandWithId =
        UpdateWorkflowCommand.builder()
            .workflowId(workflowId)
            .title(command.getTitle())
            .description(command.getDescription())
            .shopId(command.getShopId())
            .staffId(command.getStaffId())
            .targetCustomerGrades(command.getTargetCustomerGrades())
            .targetTags(command.getTargetTags())
            .excludeDormantCustomers(command.getExcludeDormantCustomers())
            .dormantPeriodMonths(command.getDormantPeriodMonths())
            .excludeRecentMessageReceivers(command.getExcludeRecentMessageReceivers())
            .recentMessagePeriodDays(command.getRecentMessagePeriodDays())
            .triggerType(command.getTriggerType())
            .triggerCategory(command.getTriggerCategory())
            .triggerConfig(command.getTriggerConfig())
            .actionType(command.getActionType())
            .actionConfig(command.getActionConfig())
            .isActive(command.getIsActive())
            .build();

    workflowCommandService.updateWorkflow(commandWithId);

    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "워크플로우 삭제", description = "기존 워크플로우를 소프트 삭제합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "워크플로우 삭제 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "워크플로우를 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "403",
        description = "워크플로우에 대한 접근 권한이 없음")
  })
  @DeleteMapping("/{workflowId}")
  public ResponseEntity<ApiResponse<Void>> deleteWorkflow(
      @Parameter(description = "워크플로우 ID", required = true, example = "1") @PathVariable
          Long workflowId,
      @Parameter(description = "워크플로우 삭제 정보", required = true) @Valid @RequestBody
          DeleteWorkflowCommand command) {
    log.info("워크플로우 삭제 요청: ID={}", workflowId);

    DeleteWorkflowCommand commandWithId =
        new DeleteWorkflowCommand(workflowId, command.getShopId(), command.getStaffId());

    workflowCommandService.deleteWorkflow(commandWithId);

    return ResponseEntity.ok(ApiResponse.success(null));
  }

  public static class WorkflowCreateResponse {
    private final Long workflowId;

    public WorkflowCreateResponse(Long workflowId) {
      this.workflowId = workflowId;
    }

    public Long getWorkflowId() {
      return workflowId;
    }
  }
}
