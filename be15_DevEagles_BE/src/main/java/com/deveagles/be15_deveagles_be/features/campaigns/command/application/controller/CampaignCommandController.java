package com.deveagles.be15_deveagles_be.features.campaigns.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.campaigns.command.application.dto.request.CreateCampaignRequest;
import com.deveagles.be15_deveagles_be.features.campaigns.command.application.dto.response.CampaignResponse;
import com.deveagles.be15_deveagles_be.features.campaigns.command.application.service.CampaignCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "캠페인 관리", description = "캠페인 생성, 삭제 API")
@RestController
@RequestMapping("/campaigns")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CampaignCommandController {

  private final CampaignCommandService campaignCommandService;

  @Operation(summary = "캠페인 생성", description = "새로운 캠페인을 생성합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "캠페인 생성 성공",
        content = @Content(schema = @Schema(implementation = CampaignResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터")
  })
  @PostMapping
  public ResponseEntity<ApiResponse<CampaignResponse>> createCampaign(
      @Parameter(description = "캠페인 생성 정보", required = true) @Valid @RequestBody
          CreateCampaignRequest request) {
    log.info("캠페인 생성 요청 - 제목: {}, 매장ID: {}", request.getCampaignTitle(), request.getShopId());

    CampaignResponse response = campaignCommandService.createCampaign(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
  }

  @Operation(summary = "캠페인 삭제", description = "캠페인을 소프트 삭제합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "캠페인 삭제 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "캠페인을 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "이미 삭제된 캠페인")
  })
  @DeleteMapping("/{campaignId}")
  public ResponseEntity<ApiResponse<String>> deleteCampaign(
      @Parameter(description = "캠페인 ID", required = true) @PathVariable Long campaignId) {
    log.info("캠페인 삭제 요청 - ID: {}", campaignId);

    campaignCommandService.deleteCampaign(campaignId);
    return ResponseEntity.ok(ApiResponse.success("캠페인이 성공적으로 삭제되었습니다."));
  }
}
