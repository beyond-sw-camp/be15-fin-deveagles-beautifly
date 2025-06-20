package com.deveagles.be15_deveagles_be.features.customers.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateAcquisitionChannelRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateAcquisitionChannelRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.AcquisitionChannelResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.application.service.AcquisitionChannelCommandService;
import com.deveagles.be15_deveagles_be.features.customers.query.service.AcquisitionChannelQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유입경로 관리", description = "유입경로 생성, 수정, 삭제, 조회 API")
@RestController
@RequestMapping("/customers/acquisition-channels")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AcquisitionChannelController {

  private final AcquisitionChannelCommandService acquisitionChannelCommandService;
  private final AcquisitionChannelQueryService acquisitionChannelQueryService;

  @Operation(summary = "유입경로 생성", description = "새로운 유입경로를 생성합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "유입경로 생성 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "중복된 유입경로명")
  })
  @PostMapping
  public ResponseEntity<ApiResponse<Long>> createAcquisitionChannel(
      @Parameter(description = "유입경로 생성 정보", required = true) @Valid @RequestBody
          CreateAcquisitionChannelRequest request) {
    log.info("유입경로 생성 요청 - 채널명: {}", request.getChannelName());

    Long channelId = acquisitionChannelCommandService.createAcquisitionChannel(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(channelId));
  }

  @Operation(summary = "유입경로 단건 조회", description = "유입경로 ID로 특정 유입경로를 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "유입경로 조회 성공",
        content = @Content(schema = @Schema(implementation = AcquisitionChannelResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "유입경로를 찾을 수 없음")
  })
  @GetMapping("/{channelId}")
  public ResponseEntity<ApiResponse<AcquisitionChannelResponse>> getAcquisitionChannel(
      @Parameter(description = "유입경로 ID", required = true, example = "1") @PathVariable
          Long channelId) {
    log.info("유입경로 단건 조회 요청 - ID: {}", channelId);

    AcquisitionChannelResponse response =
        acquisitionChannelQueryService.getAcquisitionChannel(channelId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "유입경로 페이징 조회", description = "유입경로 목록을 페이징으로 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "유입경로 목록 조회 성공")
  })
  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<AcquisitionChannelResponse>>>
      getAcquisitionChannels(
          @Parameter(description = "페이징 정보") @PageableDefault(size = 20) Pageable pageable) {
    log.info("유입경로 페이징 조회 요청 - 페이지: {}, 크기: {}", pageable.getPageNumber(), pageable.getPageSize());

    Page<AcquisitionChannelResponse> acquisitionChannels =
        acquisitionChannelQueryService.getAcquisitionChannels(pageable);
    PagedResponse<AcquisitionChannelResponse> response =
        PagedResponse.from(PagedResult.from(acquisitionChannels));
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "전체 유입경로 조회", description = "모든 유입경로를 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "전체 유입경로 조회 성공")
  })
  @GetMapping("/all")
  public ResponseEntity<ApiResponse<List<AcquisitionChannelResponse>>> getAllAcquisitionChannels() {
    log.info("전체 유입경로 조회 요청");

    List<AcquisitionChannelResponse> response =
        acquisitionChannelQueryService.getAllAcquisitionChannels();
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "유입경로 수정", description = "기존 유입경로의 정보를 수정합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "유입경로 수정 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "유입경로를 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "중복된 유입경로명")
  })
  @PutMapping("/{channelId}")
  public ResponseEntity<ApiResponse<Void>> updateAcquisitionChannel(
      @Parameter(description = "유입경로 ID", required = true, example = "1") @PathVariable
          Long channelId,
      @Parameter(description = "유입경로 수정 정보", required = true) @Valid @RequestBody
          UpdateAcquisitionChannelRequest request) {
    log.info("유입경로 수정 요청 - ID: {}, 새 채널명: {}", channelId, request.getChannelName());

    acquisitionChannelCommandService.updateAcquisitionChannel(channelId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "유입경로 삭제", description = "기존 유입경로를 삭제합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "유입경로 삭제 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "유입경로를 찾을 수 없음")
  })
  @DeleteMapping("/{channelId}")
  public ResponseEntity<ApiResponse<Void>> deleteAcquisitionChannel(
      @Parameter(description = "유입경로 ID", required = true, example = "1") @PathVariable
          Long channelId) {
    log.info("유입경로 삭제 요청 - ID: {}", channelId);

    acquisitionChannelCommandService.deleteAcquisitionChannel(channelId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
