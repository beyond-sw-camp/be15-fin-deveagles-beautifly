package com.deveagles.be15_deveagles_be.features.campaigns.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.campaigns.query.dto.request.CampaignSearchRequest;
import com.deveagles.be15_deveagles_be.features.campaigns.query.dto.response.CampaignQueryResponse;
import com.deveagles.be15_deveagles_be.features.campaigns.query.service.CampaignQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "캠페인 조회", description = "캠페인 조회 API")
@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CampaignQueryController {

  private final CampaignQueryService campaignQueryService;

  @Operation(summary = "매장별 캠페인 조회", description = "매장 ID로 캠페인을 페이징 조회합니다. 삭제되지 않은 캠페인만 조회됩니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "캠페인 조회 성공",
        content = @Content(schema = @Schema(implementation = PagedResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 파라미터")
  })
  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<CampaignQueryResponse>>> getCampaignsByShop(
      @Parameter(description = "매장 ID", required = true, example = "1") @RequestParam Long shopId,
      @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10")
          int size) {
    log.info("매장별 캠페인 조회 요청 - 매장ID: {}, 페이지: {}, 크기: {}", shopId, page, size);

    CampaignSearchRequest request = new CampaignSearchRequest(shopId, page, size);
    PagedResult<CampaignQueryResponse> pagedResult =
        campaignQueryService.getCampaignsByShop(request);
    PagedResponse<CampaignQueryResponse> response = PagedResponse.from(pagedResult);

    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "캠페인 상세 조회", description = "캠페인 ID로 특정 캠페인의 상세 정보를 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "캠페인 조회 성공",
        content = @Content(schema = @Schema(implementation = CampaignQueryResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "캠페인을 찾을 수 없음")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CampaignQueryResponse>> getCampaignById(
      @Parameter(description = "캠페인 ID", required = true, example = "1") @PathVariable Long id) {
    log.info("캠페인 상세 조회 요청 - ID: {}", id);

    CampaignQueryResponse campaign =
        campaignQueryService
            .getCampaignById(id)
            .orElseThrow(() -> new RuntimeException("캠페인을 찾을 수 없습니다."));

    return ResponseEntity.ok(ApiResponse.success(campaign));
  }
}
