package com.deveagles.be15_deveagles_be.features.campaigns.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.campaigns.command.application.dto.request.CreateCampaignRequest;
import com.deveagles.be15_deveagles_be.features.campaigns.command.application.dto.response.CampaignResponse;
import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.aggregate.Campaign;
import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.repository.CampaignRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CampaignCommandService 단위 테스트")
class CampaignCommandServiceImplTest {

  @Mock private CampaignRepository campaignRepository;

  @InjectMocks private CampaignCommandServiceImpl campaignCommandService;

  @Test
  @DisplayName("유효한 캠페인 생성 요청으로 캠페인이 성공적으로 생성된다")
  void 유효한_캠페인_생성_요청으로_캠페인이_성공적으로_생성된다() {
    // Given
    CreateCampaignRequest request =
        new CreateCampaignRequest(
            "새해 이벤트",
            "새해맞이 특가 이벤트",
            LocalDate.now().minusDays(5),
            LocalDate.now().plusDays(25),
            LocalDateTime.now().minusDays(3),
            1L,
            1L,
            1L,
            1L,
            1L,
            1L);

    Campaign savedCampaign =
        Campaign.builder()
            .id(1L)
            .campaignTitle("새해 이벤트")
            .description("새해맞이 특가 이벤트")
            .startDate(LocalDate.now().minusDays(5))
            .endDate(LocalDate.now().plusDays(25))
            .messageSendAt(LocalDateTime.now().minusDays(3))
            .staffId(1L)
            .templateId(1L)
            .couponId(1L)
            .shopId(1L)
            .createdAt(LocalDateTime.now())
            .build();

    given(campaignRepository.save(any(Campaign.class))).willReturn(savedCampaign);

    // When
    CampaignResponse response = campaignCommandService.createCampaign(request);

    // Then
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(1L);
    assertThat(response.getCampaignTitle()).isEqualTo("새해 이벤트");
    assertThat(response.getDescription()).isEqualTo("새해맞이 특가 이벤트");
    assertThat(response.getStartDate()).isEqualTo(LocalDate.now().minusDays(5));
    assertThat(response.getEndDate()).isEqualTo(LocalDate.now().plusDays(25));
    assertThat(response.getShopId()).isEqualTo(1L);

    then(campaignRepository).should(times(1)).save(any(Campaign.class));
  }

  @Test
  @DisplayName("종료일이 시작일보다 빠른 경우 예외가 발생한다")
  void 종료일이_시작일보다_빠른_경우_예외가_발생한다() {
    // Given
    CreateCampaignRequest request =
        new CreateCampaignRequest(
            "잘못된 기간 캠페인",
            "잘못된 기간 설정",
            LocalDate.now().plusDays(10),
            LocalDate.now().minusDays(5), // 종료일이 시작일보다 빠름
            LocalDateTime.now().plusDays(1),
            1L,
            1L,
            1L,
            1L,
            1L,
            1L);

    // When & Then
    assertThatThrownBy(() -> campaignCommandService.createCampaign(request))
        .isInstanceOf(BusinessException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CAMPAIGN_DATE_RANGE);

    then(campaignRepository).should(never()).save(any(Campaign.class));
  }

  @Test
  @DisplayName("존재하는 캠페인 ID로 캠페인이 성공적으로 삭제된다")
  void 존재하는_캠페인_ID로_캠페인이_성공적으로_삭제된다() {
    // Given
    Long campaignId = 1L;
    Campaign campaign =
        Campaign.builder()
            .id(campaignId)
            .campaignTitle("삭제할 캠페인")
            .description("삭제할 캠페인 설명")
            .startDate(LocalDate.now().minusDays(5))
            .endDate(LocalDate.now().plusDays(25))
            .staffId(1L)
            .templateId(1L)
            .couponId(1L)
            .shopId(1L)
            .createdAt(LocalDateTime.now())
            .build();

    given(campaignRepository.findById(campaignId)).willReturn(Optional.of(campaign));
    given(campaignRepository.save(any(Campaign.class))).willReturn(campaign);

    // When
    campaignCommandService.deleteCampaign(campaignId);

    // Then
    assertThat(campaign.isDeleted()).isTrue();
    then(campaignRepository).should(times(1)).findById(campaignId);
    then(campaignRepository).should(times(1)).save(campaign);
  }

  @Test
  @DisplayName("존재하지 않는 캠페인 ID로 삭제 시 예외가 발생한다")
  void 존재하지_않는_캠페인_ID로_삭제_시_예외가_발생한다() {
    // Given
    Long nonExistentCampaignId = 999L;
    given(campaignRepository.findById(nonExistentCampaignId)).willReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(() -> campaignCommandService.deleteCampaign(nonExistentCampaignId))
        .isInstanceOf(BusinessException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CAMPAIGN_NOT_FOUND);

    then(campaignRepository).should(times(1)).findById(nonExistentCampaignId);
    then(campaignRepository).should(never()).save(any(Campaign.class));
  }

  @Test
  @DisplayName("이미 삭제된 캠페인을 다시 삭제 시 예외가 발생한다")
  void 이미_삭제된_캠페인을_다시_삭제_시_예외가_발생한다() {
    // Given
    Long campaignId = 1L;
    Campaign deletedCampaign =
        Campaign.builder()
            .id(campaignId)
            .campaignTitle("이미 삭제된 캠페인")
            .description("이미 삭제된 캠페인 설명")
            .startDate(LocalDate.now().minusDays(5))
            .endDate(LocalDate.now().plusDays(25))
            .staffId(1L)
            .templateId(1L)
            .couponId(1L)
            .shopId(1L)
            .createdAt(LocalDateTime.now())
            .deletedAt(LocalDateTime.now())
            .build();

    given(campaignRepository.findById(campaignId)).willReturn(Optional.of(deletedCampaign));

    // When & Then
    assertThatThrownBy(() -> campaignCommandService.deleteCampaign(campaignId))
        .isInstanceOf(BusinessException.class)
        .hasFieldOrPropertyWithValue("errorCode", ErrorCode.CAMPAIGN_ALREADY_DELETED);

    then(campaignRepository).should(times(1)).findById(campaignId);
    then(campaignRepository).should(never()).save(any(Campaign.class));
  }
}
