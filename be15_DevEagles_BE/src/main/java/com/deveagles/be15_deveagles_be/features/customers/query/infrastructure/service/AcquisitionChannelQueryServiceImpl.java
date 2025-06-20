package com.deveagles.be15_deveagles_be.features.customers.query.infrastructure.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.AcquisitionChannelResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.AcquisitionChannel;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.AcquisitionChannelRepository;
import com.deveagles.be15_deveagles_be.features.customers.query.service.AcquisitionChannelQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AcquisitionChannelQueryServiceImpl implements AcquisitionChannelQueryService {

  private final AcquisitionChannelRepository acquisitionChannelRepository;

  @Override
  public AcquisitionChannelResponse getAcquisitionChannel(Long channelId) {
    log.info("유입경로 단건 조회 요청 - ID: {}", channelId);

    AcquisitionChannel acquisitionChannel =
        acquisitionChannelRepository
            .findById(channelId)
            .orElseThrow(
                () -> {
                  log.error("유입경로를 찾을 수 없음 - ID: {}", channelId);
                  return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "유입경로를 찾을 수 없습니다.");
                });

    AcquisitionChannelResponse response = mapToResponse(acquisitionChannel);
    log.info("유입경로 조회 완료 - ID: {}, 채널명: {}", channelId, response.getChannelName());

    return response;
  }

  @Override
  public List<AcquisitionChannelResponse> getAllAcquisitionChannels() {
    log.info("전체 유입경로 목록 조회 요청");

    List<AcquisitionChannel> acquisitionChannels = acquisitionChannelRepository.findAll();
    List<AcquisitionChannelResponse> responses =
        acquisitionChannels.stream().map(this::mapToResponse).toList();

    log.info("전체 유입경로 목록 조회 완료 - 총 {}개", responses.size());
    return responses;
  }

  @Override
  public Page<AcquisitionChannelResponse> getAcquisitionChannels(Pageable pageable) {
    log.info("유입경로 페이징 조회 요청 - 페이지: {}, 크기: {}", pageable.getPageNumber(), pageable.getPageSize());

    Page<AcquisitionChannel> acquisitionChannels = acquisitionChannelRepository.findAll(pageable);
    Page<AcquisitionChannelResponse> responses = acquisitionChannels.map(this::mapToResponse);

    log.info(
        "유입경로 페이징 조회 완료 - 현재페이지: {}, 전체페이지: {}, 전체개수: {}",
        responses.getNumber(),
        responses.getTotalPages(),
        responses.getTotalElements());

    return responses;
  }

  private AcquisitionChannelResponse mapToResponse(AcquisitionChannel acquisitionChannel) {
    return AcquisitionChannelResponse.builder()
        .id(acquisitionChannel.getId())
        .channelName(acquisitionChannel.getChannelName())
        .createdAt(acquisitionChannel.getCreatedAt())
        .modifiedAt(acquisitionChannel.getModifiedAt())
        .build();
  }
}
