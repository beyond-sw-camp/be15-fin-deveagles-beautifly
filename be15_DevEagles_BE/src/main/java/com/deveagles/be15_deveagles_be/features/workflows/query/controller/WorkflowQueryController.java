package com.deveagles.be15_deveagles_be.features.workflows.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.request.WorkflowSearchRequest;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowQueryResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowStatsResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowSummaryResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.service.WorkflowQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "워크플로우 조회", description = "워크플로우 조회 및 검색 API")
@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
@Validated
@Slf4j
public class WorkflowQueryController {

  private final WorkflowQueryService workflowQueryService;

  @Operation(summary = "워크플로우 상세 조회", description = "워크플로우 ID로 상세 정보를 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "워크플로우 조회 성공",
        content = @Content(schema = @Schema(implementation = WorkflowQueryResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "워크플로우를 찾을 수 없음")
  })
  @GetMapping("/{workflowId}")
  public ResponseEntity<ApiResponse<WorkflowQueryResponse>> getWorkflow(
      @Parameter(description = "워크플로우 ID", required = true) @PathVariable Long workflowId,
      @Parameter(description = "매장 ID", required = true) @RequestParam Long shopId) {

    log.info("워크플로우 상세 조회 요청 - workflowId: {}, shopId: {}", workflowId, shopId);

    WorkflowQueryResponse response = workflowQueryService.getWorkflowById(workflowId, shopId);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "워크플로우 목록 검색", description = "다양한 조건으로 워크플로우를 검색하고 페이징 결과를 반환합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "워크플로우 검색 성공")
  })
  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<WorkflowSummaryResponse>>> searchWorkflows(
      @Parameter(description = "매장 ID", required = true) @RequestParam Long shopId,
      @Parameter(description = "검색어 (제목, 설명)") @RequestParam(required = false) String searchQuery,
      @Parameter(description = "상태 필터 (active, inactive, all)") @RequestParam(required = false)
          String statusFilter,
      @Parameter(description = "트리거 카테고리") @RequestParam(required = false) String triggerCategory,
      @Parameter(description = "트리거 타입") @RequestParam(required = false) String triggerType,
      @Parameter(description = "액션 타입") @RequestParam(required = false) String actionType,
      @Parameter(description = "활성화 상태") @RequestParam(required = false) Boolean isActive,
      @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
      @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "createdAt") String sortBy,
      @Parameter(description = "정렬 방향") @RequestParam(defaultValue = "desc") String sortDirection) {

    log.info("워크플로우 목록 검색 요청 - shopId: {}, page: {}, size: {}", shopId, page, size);

    WorkflowSearchRequest request =
        WorkflowSearchRequest.builder()
            .shopId(shopId)
            .searchQuery(searchQuery)
            .statusFilter(statusFilter)
            .triggerCategory(triggerCategory)
            .triggerType(triggerType)
            .actionType(actionType)
            .isActive(isActive)
            .page(page)
            .size(size)
            .sortBy(sortBy)
            .sortDirection(sortDirection)
            .build();

    PagedResponse<WorkflowSummaryResponse> response = workflowQueryService.searchWorkflows(request);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "워크플로우 통계 조회", description = "매장의 워크플로우 관련 통계 정보를 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "통계 조회 성공",
        content = @Content(schema = @Schema(implementation = WorkflowStatsResponse.class)))
  })
  @GetMapping("/stats")
  public ResponseEntity<ApiResponse<WorkflowStatsResponse>> getWorkflowStats(
      @Parameter(description = "매장 ID", required = true) @RequestParam Long shopId) {

    log.info("워크플로우 통계 조회 요청 - shopId: {}", shopId);

    WorkflowStatsResponse response = workflowQueryService.getWorkflowStats(shopId);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "트리거 카테고리별 워크플로우 조회", description = "특정 트리거 카테고리에 속하는 워크플로우 목록을 조회합니다.")
  @GetMapping("/trigger-category/{triggerCategory}")
  public ResponseEntity<ApiResponse<List<WorkflowSummaryResponse>>> getWorkflowsByTriggerCategory(
      @Parameter(description = "트리거 카테고리", required = true) @PathVariable String triggerCategory,
      @Parameter(description = "매장 ID", required = true) @RequestParam Long shopId) {

    log.info("트리거 카테고리별 워크플로우 조회 요청 - triggerCategory: {}, shopId: {}", triggerCategory, shopId);

    List<WorkflowSummaryResponse> response =
        workflowQueryService.getWorkflowsByTriggerCategory(triggerCategory, shopId);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "트리거 타입별 워크플로우 조회", description = "특정 트리거 타입에 속하는 워크플로우 목록을 조회합니다.")
  @GetMapping("/trigger-type/{triggerType}")
  public ResponseEntity<ApiResponse<List<WorkflowSummaryResponse>>> getWorkflowsByTriggerType(
      @Parameter(description = "트리거 타입", required = true) @PathVariable String triggerType,
      @Parameter(description = "매장 ID", required = true) @RequestParam Long shopId) {

    log.info("트리거 타입별 워크플로우 조회 요청 - triggerType: {}, shopId: {}", triggerType, shopId);

    List<WorkflowSummaryResponse> response =
        workflowQueryService.getWorkflowsByTriggerType(triggerType, shopId);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "최근 워크플로우 조회", description = "최근 생성된 워크플로우 목록을 조회합니다.")
  @GetMapping("/recent")
  public ResponseEntity<ApiResponse<List<WorkflowSummaryResponse>>> getRecentWorkflows(
      @Parameter(description = "매장 ID", required = true) @RequestParam Long shopId,
      @Parameter(description = "조회할 개수") @RequestParam(defaultValue = "10") int limit) {

    log.info("최근 워크플로우 조회 요청 - shopId: {}, limit: {}", shopId, limit);

    List<WorkflowSummaryResponse> response = workflowQueryService.getRecentWorkflows(shopId, limit);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "활성 워크플로우 조회", description = "활성화된 워크플로우 목록을 조회합니다.")
  @GetMapping("/active")
  public ResponseEntity<ApiResponse<List<WorkflowSummaryResponse>>> getActiveWorkflows(
      @Parameter(description = "매장 ID", required = true) @RequestParam Long shopId) {

    log.info("활성 워크플로우 조회 요청 - shopId: {}", shopId);

    List<WorkflowSummaryResponse> response = workflowQueryService.getActiveWorkflows(shopId);

    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
