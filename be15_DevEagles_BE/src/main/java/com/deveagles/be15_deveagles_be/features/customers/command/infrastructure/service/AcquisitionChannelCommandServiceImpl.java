package com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateAcquisitionChannelRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateAcquisitionChannelRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.service.AcquisitionChannelCommandService;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.AcquisitionChannel;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.AcquisitionChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AcquisitionChannelCommandServiceImpl implements AcquisitionChannelCommandService {

  private final AcquisitionChannelRepository acquisitionChannelRepository;

  @Override
  public Long createAcquisitionChannel(CreateAcquisitionChannelRequest request) {
    log.info("유입경로 생성 요청 - 채널명: {}", request.getChannelName());

    validateChannelNameNotExists(request.getChannelName());

    AcquisitionChannel acquisitionChannel =
        AcquisitionChannel.builder().channelName(request.getChannelName()).build();

    AcquisitionChannel savedChannel = acquisitionChannelRepository.save(acquisitionChannel);

    log.info("유입경로 생성 완료 - ID: {}, 채널명: {}", savedChannel.getId(), savedChannel.getChannelName());
    return savedChannel.getId();
  }

  @Override
  public void updateAcquisitionChannel(Long channelId, UpdateAcquisitionChannelRequest request) {
    log.info("유입경로 수정 요청 - ID: {}, 새 채널명: {}", channelId, request.getChannelName());

    AcquisitionChannel acquisitionChannel = findAcquisitionChannelById(channelId);

    if (!acquisitionChannel.getChannelName().equals(request.getChannelName())) {
      validateChannelNameNotExists(request.getChannelName());
    }

    String oldChannelName = acquisitionChannel.getChannelName();
    acquisitionChannel.updateChannelName(request.getChannelName());

    log.info(
        "유입경로 수정 완료 - ID: {}, 기존명: {}, 새명: {}",
        channelId,
        oldChannelName,
        request.getChannelName());
  }

  @Override
  public void deleteAcquisitionChannel(Long channelId) {
    log.info("유입경로 삭제 요청 - ID: {}", channelId);

    AcquisitionChannel acquisitionChannel = findAcquisitionChannelById(channelId);
    acquisitionChannelRepository.delete(acquisitionChannel);

    log.info("유입경로 삭제 완료 - ID: {}, 채널명: {}", channelId, acquisitionChannel.getChannelName());
  }

  private AcquisitionChannel findAcquisitionChannelById(Long channelId) {
    return acquisitionChannelRepository
        .findById(channelId)
        .orElseThrow(
            () -> {
              log.error("유입경로를 찾을 수 없음 - ID: {}", channelId);
              return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "유입경로를 찾을 수 없습니다.");
            });
  }

  private void validateChannelNameNotExists(String channelName) {
    if (acquisitionChannelRepository.existsByChannelName(channelName)) {
      log.error("중복된 유입경로명 - 채널명: {}", channelName);
      throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 유입경로명입니다.");
    }
  }
}
