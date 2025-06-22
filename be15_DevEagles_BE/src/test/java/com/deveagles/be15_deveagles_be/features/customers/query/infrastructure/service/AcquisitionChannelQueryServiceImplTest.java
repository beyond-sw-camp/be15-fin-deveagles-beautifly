package com.deveagles.be15_deveagles_be.features.customers.query.infrastructure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.AcquisitionChannelResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.AcquisitionChannel;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.AcquisitionChannelRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("유입경로 쿼리 서비스 테스트")
class AcquisitionChannelQueryServiceImplTest {

  @Mock private AcquisitionChannelRepository acquisitionChannelRepository;

  @InjectMocks private AcquisitionChannelQueryServiceImpl acquisitionChannelQueryService;

  @Test
  @DisplayName("유입경로 단건 조회 성공")
  void getAcquisitionChannel_Success() {
    // given
    Long channelId = 1L;
    AcquisitionChannel channel = createTestAcquisitionChannel(channelId, "네이버 블로그");

    given(acquisitionChannelRepository.findById(channelId)).willReturn(Optional.of(channel));

    // when
    AcquisitionChannelResponse response =
        acquisitionChannelQueryService.getAcquisitionChannel(channelId);

    // then
    assertThat(response.getId()).isEqualTo(channelId);
    assertThat(response.getChannelName()).isEqualTo("네이버 블로그");
    assertThat(response.getCreatedAt()).isNotNull();
    assertThat(response.getModifiedAt()).isNotNull();

    then(acquisitionChannelRepository).should().findById(channelId);
  }

  @Test
  @DisplayName("유입경로 단건 조회 실패 - 존재하지 않는 채널")
  void getAcquisitionChannel_NotFound() {
    // given
    Long channelId = 1L;
    given(acquisitionChannelRepository.findById(channelId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> acquisitionChannelQueryService.getAcquisitionChannel(channelId))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("유입경로를 찾을 수 없습니다.");

    then(acquisitionChannelRepository).should().findById(channelId);
  }

  @Test
  @DisplayName("전체 유입경로 목록 조회 성공")
  void getAllAcquisitionChannels_Success() {
    // given
    List<AcquisitionChannel> channels =
        List.of(
            createTestAcquisitionChannel(1L, "네이버 블로그"),
            createTestAcquisitionChannel(2L, "구글 광고"),
            createTestAcquisitionChannel(3L, "지인 추천"));

    given(acquisitionChannelRepository.findAll()).willReturn(channels);

    // when
    List<AcquisitionChannelResponse> responses =
        acquisitionChannelQueryService.getAllAcquisitionChannels();

    // then
    assertThat(responses).hasSize(3);
    assertThat(responses.get(0).getChannelName()).isEqualTo("네이버 블로그");
    assertThat(responses.get(1).getChannelName()).isEqualTo("구글 광고");
    assertThat(responses.get(2).getChannelName()).isEqualTo("지인 추천");

    then(acquisitionChannelRepository).should().findAll();
  }

  @Test
  @DisplayName("전체 유입경로 목록 조회 - 빈 목록")
  void getAllAcquisitionChannels_EmptyList() {
    // given
    given(acquisitionChannelRepository.findAll()).willReturn(List.of());

    // when
    List<AcquisitionChannelResponse> responses =
        acquisitionChannelQueryService.getAllAcquisitionChannels();

    // then
    assertThat(responses).isEmpty();

    then(acquisitionChannelRepository).should().findAll();
  }

  @Test
  @DisplayName("유입경로 페이징 조회 성공")
  void getAcquisitionChannels_Success() {
    // given
    Pageable pageable = PageRequest.of(0, 2);
    List<AcquisitionChannel> channels =
        List.of(
            createTestAcquisitionChannel(1L, "네이버 블로그"), createTestAcquisitionChannel(2L, "구글 광고"));
    Page<AcquisitionChannel> channelPage = new PageImpl<>(channels, pageable, 5);

    given(acquisitionChannelRepository.findAll(pageable)).willReturn(channelPage);

    // when
    Page<AcquisitionChannelResponse> responses =
        acquisitionChannelQueryService.getAcquisitionChannels(pageable);

    // then
    assertThat(responses.getContent()).hasSize(2);
    assertThat(responses.getTotalElements()).isEqualTo(5);
    assertThat(responses.getTotalPages()).isEqualTo(3);
    assertThat(responses.getNumber()).isEqualTo(0);
    assertThat(responses.getSize()).isEqualTo(2);

    assertThat(responses.getContent().get(0).getChannelName()).isEqualTo("네이버 블로그");
    assertThat(responses.getContent().get(1).getChannelName()).isEqualTo("구글 광고");

    then(acquisitionChannelRepository).should().findAll(pageable);
  }

  @Test
  @DisplayName("유입경로 페이징 조회 - 빈 페이지")
  void getAcquisitionChannels_EmptyPage() {
    // given
    Pageable pageable = PageRequest.of(0, 10);
    Page<AcquisitionChannel> emptyPage = new PageImpl<>(List.of(), pageable, 0);

    given(acquisitionChannelRepository.findAll(pageable)).willReturn(emptyPage);

    // when
    Page<AcquisitionChannelResponse> responses =
        acquisitionChannelQueryService.getAcquisitionChannels(pageable);

    // then
    assertThat(responses.getContent()).isEmpty();
    assertThat(responses.getTotalElements()).isEqualTo(0);
    assertThat(responses.getTotalPages()).isEqualTo(0);

    then(acquisitionChannelRepository).should().findAll(pageable);
  }

  private AcquisitionChannel createTestAcquisitionChannel(Long id, String channelName) {
    return AcquisitionChannel.builder()
        .id(id)
        .channelName(channelName)
        .createdAt(LocalDateTime.now())
        .modifiedAt(LocalDateTime.now())
        .build();
  }
}
