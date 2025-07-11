package com.deveagles.be15_deveagles_be.features.customers.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateTagRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateTagRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.TagResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.application.service.TagCommandService;
import com.deveagles.be15_deveagles_be.features.customers.query.service.TagQueryService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "태그 관리", description = "태그 생성, 수정, 삭제, 조회 API")
@RestController
@RequestMapping("/customers/tags")
@RequiredArgsConstructor
@Validated
@Slf4j
public class TagController {

  private final TagCommandService tagCommandService;
  private final TagQueryService tagQueryService;

  @Operation(summary = "태그 생성", description = "새로운 태그를 생성합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "태그 생성 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "중복된 태그명")
  })
  @PostMapping
  public ResponseEntity<ApiResponse<Long>> createTag(
      @AuthenticationPrincipal CustomUser user,
      @Parameter(description = "태그 생성 정보", required = true) @Valid @RequestBody
          CreateTagRequest request) {
    log.info(
        "태그 생성 요청 - 매장ID: {}, 태그명: {}, 색상코드: {}",
        user.getShopId(),
        request.getTagName(),
        request.getColorCode());

    CreateTagRequest requestWithShopId =
        new CreateTagRequest(user.getShopId(), request.getTagName(), request.getColorCode());

    Long tagId = tagCommandService.createTag(requestWithShopId);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(tagId));
  }

  @Operation(summary = "태그 단건 조회", description = "태그 ID로 특정 태그를 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "태그 조회 성공",
        content = @Content(schema = @Schema(implementation = TagResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "태그를 찾을 수 없음")
  })
  @GetMapping("/{tagId}")
  public ResponseEntity<ApiResponse<TagResponse>> getTag(
      @AuthenticationPrincipal CustomUser user,
      @Parameter(description = "태그 ID", required = true, example = "1") @PathVariable Long tagId) {
    log.info("태그 단건 조회 요청 - ID: {}, 매장ID: {}", tagId, user.getShopId());

    TagResponse response = tagQueryService.getTag(tagId, user.getShopId());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "매장별 전체 태그 조회", description = "특정 매장의 모든 태그를 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "전체 태그 조회 성공")
  })
  @GetMapping
  public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags(
      @AuthenticationPrincipal CustomUser user) {
    log.info("매장별 전체 태그 조회 요청 - 매장ID: {}", user.getShopId());

    List<TagResponse> response = tagQueryService.getAllTagsByShopId(user.getShopId());
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "태그 수정", description = "기존 태그의 정보를 수정합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "태그 수정 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "태그를 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "중복된 태그명")
  })
  @PutMapping("/{tagId}")
  public ResponseEntity<ApiResponse<Void>> updateTag(
      @AuthenticationPrincipal CustomUser user,
      @Parameter(description = "태그 ID", required = true, example = "1") @PathVariable Long tagId,
      @Parameter(description = "태그 수정 정보", required = true) @Valid @RequestBody
          UpdateTagRequest request) {
    log.info(
        "태그 수정 요청 - ID: {}, 매장ID: {}, 새 태그명: {}, 새 색상코드: {}",
        tagId,
        user.getShopId(),
        request.getTagName(),
        request.getColorCode());

    // shopId를 user에서 가져와서 새로운 request 생성
    UpdateTagRequest requestWithShopId =
        new UpdateTagRequest(user.getShopId(), request.getTagName(), request.getColorCode());

    tagCommandService.updateTag(tagId, requestWithShopId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "태그 삭제", description = "기존 태그를 삭제합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "태그 삭제 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "태그를 찾을 수 없음")
  })
  @DeleteMapping("/{tagId}")
  public ResponseEntity<ApiResponse<Void>> deleteTag(
      @Parameter(description = "태그 ID", required = true, example = "1") @PathVariable Long tagId) {
    log.info("태그 삭제 요청 - ID: {}", tagId);

    tagCommandService.deleteTag(tagId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
