package com.deveagles.be15_deveagles_be.features.customers.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.customers.command.application.AcquisitionChannelCommandService;
import com.deveagles.be15_deveagles_be.features.customers.command.dto.CreateAcquisitionChannelRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.dto.UpdateAcquisitionChannelRequest;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.AcquisitionChannelResponse;
import com.deveagles.be15_deveagles_be.features.customers.query.service.AcquisitionChannelQueryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers/acquisition-channels")
@RequiredArgsConstructor
public class AcquisitionChannelController {

  private final AcquisitionChannelCommandService acquisitionChannelCommandService;
  private final AcquisitionChannelQueryService acquisitionChannelQueryService;

  @PostMapping
  public ResponseEntity<ApiResponse<Long>> createAcquisitionChannel(
      @Valid @RequestBody CreateAcquisitionChannelRequest request) {
    Long channelId = acquisitionChannelCommandService.createAcquisitionChannel(request);
    return ResponseEntity.ok(ApiResponse.success(channelId));
  }

  @GetMapping("/{channelId}")
  public ResponseEntity<ApiResponse<AcquisitionChannelResponse>> getAcquisitionChannel(
      @PathVariable Long channelId) {
    AcquisitionChannelResponse response =
        acquisitionChannelQueryService.getAcquisitionChannel(channelId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<AcquisitionChannelResponse>>>
      getAcquisitionChannels(@PageableDefault(size = 20) Pageable pageable) {
    Page<AcquisitionChannelResponse> acquisitionChannels =
        acquisitionChannelQueryService.getAcquisitionChannels(pageable);
    PagedResponse<AcquisitionChannelResponse> response =
        PagedResponse.from(PagedResult.from(acquisitionChannels));
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @GetMapping("/all")
  public ResponseEntity<ApiResponse<List<AcquisitionChannelResponse>>> getAllAcquisitionChannels() {
    List<AcquisitionChannelResponse> response =
        acquisitionChannelQueryService.getAllAcquisitionChannels();
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @PutMapping("/{channelId}")
  public ResponseEntity<ApiResponse<Void>> updateAcquisitionChannel(
      @PathVariable Long channelId, @Valid @RequestBody UpdateAcquisitionChannelRequest request) {
    acquisitionChannelCommandService.updateAcquisitionChannel(channelId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @DeleteMapping("/{channelId}")
  public ResponseEntity<ApiResponse<Void>> deleteAcquisitionChannel(@PathVariable Long channelId) {
    acquisitionChannelCommandService.deleteAcquisitionChannel(channelId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
