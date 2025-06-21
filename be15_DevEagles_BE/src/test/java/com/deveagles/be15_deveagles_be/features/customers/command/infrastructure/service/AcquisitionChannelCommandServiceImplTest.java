package com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateAcquisitionChannelRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateAcquisitionChannelRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.AcquisitionChannel;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.AcquisitionChannelRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("유입경로 커맨드 서비스 테스트")
class AcquisitionChannelCommandServiceImplTest {

  @Mock private AcquisitionChannelRepository acquisitionChannelRepository;

  @InjectMocks private AcquisitionChannelCommandServiceImpl acquisitionChannelCommandService;

  @Test
  @DisplayName("유입경로 생성 성공")
  void createAcquisitionChannel_Success() {
    // given
    String channelName = "네이버 블로그";
    CreateAcquisitionChannelRequest request = new CreateAcquisitionChannelRequest(channelName);

    AcquisitionChannel savedChannel =
        AcquisitionChannel.builder()
            .id(1L)
            .channelName(channelName)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    given(acquisitionChannelRepository.existsByChannelName(channelName)).willReturn(false);
    given(acquisitionChannelRepository.save(any(AcquisitionChannel.class)))
        .willReturn(savedChannel);

    // when
    Long channelId = acquisitionChannelCommandService.createAcquisitionChannel(request);

    // then
    assertThat(channelId).isEqualTo(1L);
    then(acquisitionChannelRepository).should().existsByChannelName(channelName);
    then(acquisitionChannelRepository).should().save(any(AcquisitionChannel.class));
  }

  @Test
  @DisplayName("유입경로 생성 실패 - 중복된 채널명")
  void createAcquisitionChannel_DuplicateChannelName() {
    // given
    String channelName = "네이버 블로그";
    CreateAcquisitionChannelRequest request = new CreateAcquisitionChannelRequest(channelName);

    given(acquisitionChannelRepository.existsByChannelName(channelName)).willReturn(true);

    // when & then
    assertThatThrownBy(() -> acquisitionChannelCommandService.createAcquisitionChannel(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("이미 존재하는 유입경로명입니다.");

    then(acquisitionChannelRepository).should().existsByChannelName(channelName);
    then(acquisitionChannelRepository).should(never()).save(any(AcquisitionChannel.class));
  }

  @Test
  @DisplayName("유입경로 수정 성공")
  void updateAcquisitionChannel_Success() {
    // given
    Long channelId = 1L;
    String oldChannelName = "네이버 블로그";
    String newChannelName = "구글 광고";
    UpdateAcquisitionChannelRequest request = new UpdateAcquisitionChannelRequest(newChannelName);

    AcquisitionChannel existingChannel =
        AcquisitionChannel.builder()
            .id(channelId)
            .channelName(oldChannelName)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    given(acquisitionChannelRepository.findById(channelId))
        .willReturn(Optional.of(existingChannel));
    given(acquisitionChannelRepository.existsByChannelName(newChannelName)).willReturn(false);

    // when
    acquisitionChannelCommandService.updateAcquisitionChannel(channelId, request);

    // then
    then(acquisitionChannelRepository).should().findById(channelId);
    then(acquisitionChannelRepository).should().existsByChannelName(newChannelName);
  }

  @Test
  @DisplayName("유입경로 수정 실패 - 존재하지 않는 채널")
  void updateAcquisitionChannel_ChannelNotFound() {
    // given
    Long channelId = 1L;
    UpdateAcquisitionChannelRequest request = new UpdateAcquisitionChannelRequest("구글 광고");

    given(acquisitionChannelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(
            () -> acquisitionChannelCommandService.updateAcquisitionChannel(channelId, request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("유입경로를 찾을 수 없습니다.");

    then(acquisitionChannelRepository).should().findById(channelId);
    then(acquisitionChannelRepository).should(never()).existsByChannelName(any());
  }

  @Test
  @DisplayName("유입경로 수정 실패 - 중복된 채널명")
  void updateAcquisitionChannel_DuplicateChannelName() {
    // given
    Long channelId = 1L;
    String oldChannelName = "네이버 블로그";
    String newChannelName = "구글 광고";
    UpdateAcquisitionChannelRequest request = new UpdateAcquisitionChannelRequest(newChannelName);

    AcquisitionChannel existingChannel =
        AcquisitionChannel.builder()
            .id(channelId)
            .channelName(oldChannelName)
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    given(acquisitionChannelRepository.findById(channelId))
        .willReturn(Optional.of(existingChannel));
    given(acquisitionChannelRepository.existsByChannelName(newChannelName)).willReturn(true);

    // when & then
    assertThatThrownBy(
            () -> acquisitionChannelCommandService.updateAcquisitionChannel(channelId, request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("이미 존재하는 유입경로명입니다.");

    then(acquisitionChannelRepository).should().findById(channelId);
    then(acquisitionChannelRepository).should().existsByChannelName(newChannelName);
  }

  @Test
  @DisplayName("유입경로 삭제 성공")
  void deleteAcquisitionChannel_Success() {
    // given
    Long channelId = 1L;
    AcquisitionChannel existingChannel =
        AcquisitionChannel.builder()
            .id(channelId)
            .channelName("네이버 블로그")
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    given(acquisitionChannelRepository.findById(channelId))
        .willReturn(Optional.of(existingChannel));

    // when
    acquisitionChannelCommandService.deleteAcquisitionChannel(channelId);

    // then
    then(acquisitionChannelRepository).should().findById(channelId);
    then(acquisitionChannelRepository).should().delete(existingChannel);
  }

  @Test
  @DisplayName("유입경로 삭제 실패 - 존재하지 않는 채널")
  void deleteAcquisitionChannel_ChannelNotFound() {
    // given
    Long channelId = 1L;
    given(acquisitionChannelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> acquisitionChannelCommandService.deleteAcquisitionChannel(channelId))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("유입경로를 찾을 수 없습니다.");

    then(acquisitionChannelRepository).should().findById(channelId);
    then(acquisitionChannelRepository).should(never()).delete(any());
  }
}
