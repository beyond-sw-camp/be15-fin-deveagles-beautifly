package com.deveagles.be15_deveagles_be.features.customers.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateCustomerGradeRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateCustomerGradeRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.CustomerGradeResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.application.service.CustomerGradeCommandService;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerGradeQueryService;
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

@Tag(name = "고객등급 관리", description = "고객등급 생성, 수정, 삭제, 조회 API")
@RestController
@RequestMapping("/customers/grades")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CustomerGradeController {

  private final CustomerGradeCommandService customerGradeCommandService;
  private final CustomerGradeQueryService customerGradeQueryService;

  @Operation(summary = "고객등급 생성", description = "새로운 고객등급을 생성합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "고객등급 생성 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "중복된 고객등급명")
  })
  @PostMapping
  public ResponseEntity<ApiResponse<Long>> createCustomerGrade(
      @Parameter(description = "고객등급 생성 정보", required = true) @Valid @RequestBody
          CreateCustomerGradeRequest request) {
    log.info(
        "고객등급 생성 요청 - 매장ID: {}, 등급명: {}, 할인율: {}%",
        request.getShopId(), request.getCustomerGradeName(), request.getDiscountRate());

    Long gradeId = customerGradeCommandService.createCustomerGrade(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(gradeId));
  }

  @Operation(summary = "고객등급 단건 조회", description = "고객등급 ID로 특정 고객등급을 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "고객등급 조회 성공",
        content = @Content(schema = @Schema(implementation = CustomerGradeResponse.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "고객등급을 찾을 수 없음")
  })
  @GetMapping("/{gradeId}")
  public ResponseEntity<ApiResponse<CustomerGradeResponse>> getCustomerGrade(
      @Parameter(description = "고객등급 ID", required = true, example = "1") @PathVariable
          Long gradeId,
      @Parameter(description = "매장 ID", required = true, example = "1") @RequestParam Long shopId) {
    log.info("고객등급 단건 조회 요청 - ID: {}, 매장ID: {}", gradeId, shopId);

    CustomerGradeResponse response = customerGradeQueryService.getCustomerGrade(gradeId, shopId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "고객등급 페이징 조회", description = "고객등급 목록을 페이징으로 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "고객등급 목록 조회 성공")
  })
  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<CustomerGradeResponse>>> getCustomerGrades(
      @Parameter(description = "페이징 정보") @PageableDefault(size = 20) Pageable pageable) {
    log.info("고객등급 페이징 조회 요청 - 페이지: {}, 크기: {}", pageable.getPageNumber(), pageable.getPageSize());

    Page<CustomerGradeResponse> customerGrades =
        customerGradeQueryService.getCustomerGrades(pageable);
    PagedResponse<CustomerGradeResponse> response =
        PagedResponse.from(PagedResult.from(customerGrades));
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "매장별 전체 고객등급 조회", description = "특정 매장의 모든 고객등급을 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "전체 고객등급 조회 성공")
  })
  @GetMapping("/shop/{shopId}")
  public ResponseEntity<ApiResponse<List<CustomerGradeResponse>>> getAllCustomerGradesByShopId(
      @Parameter(description = "매장 ID", required = true, example = "1") @PathVariable Long shopId) {
    log.info("매장별 전체 고객등급 조회 요청 - 매장ID: {}", shopId);

    List<CustomerGradeResponse> response =
        customerGradeQueryService.getAllCustomerGradesByShopId(shopId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "고객등급 수정", description = "기존 고객등급의 정보를 수정합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "고객등급 수정 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "고객등급을 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "중복된 고객등급명")
  })
  @PutMapping("/{gradeId}")
  public ResponseEntity<ApiResponse<Void>> updateCustomerGrade(
      @Parameter(description = "고객등급 ID", required = true, example = "1") @PathVariable
          Long gradeId,
      @Parameter(description = "고객등급 수정 정보", required = true) @Valid @RequestBody
          UpdateCustomerGradeRequest request) {
    log.info(
        "고객등급 수정 요청 - ID: {}, 매장ID: {}, 새 등급명: {}, 새 할인율: {}%",
        gradeId, request.getShopId(), request.getCustomerGradeName(), request.getDiscountRate());

    customerGradeCommandService.updateCustomerGrade(gradeId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "고객등급 삭제", description = "기존 고객등급을 삭제합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "고객등급 삭제 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "고객등급을 찾을 수 없음")
  })
  @DeleteMapping("/{gradeId}")
  public ResponseEntity<ApiResponse<Void>> deleteCustomerGrade(
      @Parameter(description = "고객등급 ID", required = true, example = "1") @PathVariable
          Long gradeId) {
    log.info("고객등급 삭제 요청 - ID: {}", gradeId);

    customerGradeCommandService.deleteCustomerGrade(gradeId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
