package com.deveagles.be15_deveagles_be.features.campaigns.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.aggregate.Campaign;
import com.deveagles.be15_deveagles_be.features.campaigns.query.dto.request.CampaignSearchRequest;
import com.deveagles.be15_deveagles_be.features.campaigns.query.dto.response.CampaignQueryResponse;
import com.deveagles.be15_deveagles_be.features.campaigns.query.repository.CampaignQueryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
@DisplayName("CampaignQueryService 단위 테스트")
class CampaignQueryServiceImplTest {

  @Mock private CampaignQueryRepository campaignQueryRepository;

  @InjectMocks private CampaignQueryServiceImpl campaignQueryService;

  @Test
  @DisplayName("매장 ID로 캠페인 목록을 성공적으로 조회한다")
  void 매장_ID로_캠페인_목록을_성공적으로_조회한다() {
    // Given
    Long shopId = 1L;
    CampaignSearchRequest request = new CampaignSearchRequest(shopId, 0, 10);

    Campaign campaign1 =
        Campaign.builder()
            .id(1L)
            .campaignTitle("첫 번째 캠페인")
            .description("첫 번째 캠페인 설명")
            .startDate(LocalDate.now().minusDays(10))
            .endDate(LocalDate.now().plusDays(10))
            .messageSendAt(LocalDateTime.now().minusDays(5))
            .staffId(1L)
            .templateId(1L)
            .couponId(1L)
            .shopId(shopId)
            .createdAt(LocalDateTime.now().minusDays(15))
            .build();

    Campaign campaign2 =
        Campaign.builder()
            .id(2L)
            .campaignTitle("두 번째 캠페인")
            .description("두 번째 캠페인 설명")
            .startDate(LocalDate.now().minusDays(20))
            .endDate(LocalDate.now().plusDays(20))
            .messageSendAt(LocalDateTime.now().minusDays(10))
            .staffId(1L)
            .templateId(2L)
            .couponId(2L)
            .shopId(shopId)
            .createdAt(LocalDateTime.now().minusDays(25))
            .build();

    List<Campaign> campaigns = Arrays.asList(campaign1, campaign2);
    Page<Campaign> campaignPage = new PageImpl<>(campaigns, PageRequest.of(0, 10), 2);

    given(
            campaignQueryRepository.findByShopIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                eq(shopId), any(Pageable.class)))
        .willReturn(campaignPage);

    // When
    PagedResult<CampaignQueryResponse> result = campaignQueryService.getCampaignsByShop(request);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getPagination().getTotalItems()).isEqualTo(2);
    assertThat(result.getPagination().getTotalPages()).isEqualTo(1);
    assertThat(result.getPagination().getCurrentPage()).isEqualTo(0);

    // 첫 번째 캠페인 검증
    CampaignQueryResponse firstResponse = result.getContent().get(0);
    assertThat(firstResponse.getId()).isEqualTo(1L);
    assertThat(firstResponse.getCampaignTitle()).isEqualTo("첫 번째 캠페인");
    assertThat(firstResponse.getDescription()).isEqualTo("첫 번째 캠페인 설명");
    assertThat(firstResponse.getShopId()).isEqualTo(shopId);
    assertThat(firstResponse.isActive()).isTrue(); // 현재 날짜 기준으로 활성 상태

    // 두 번째 캠페인 검증
    CampaignQueryResponse secondResponse = result.getContent().get(1);
    assertThat(secondResponse.getId()).isEqualTo(2L);
    assertThat(secondResponse.getCampaignTitle()).isEqualTo("두 번째 캠페인");
    assertThat(secondResponse.getDescription()).isEqualTo("두 번째 캠페인 설명");
    assertThat(secondResponse.getShopId()).isEqualTo(shopId);

    then(campaignQueryRepository)
        .should(times(1))
        .findByShopIdAndDeletedAtIsNullOrderByCreatedAtDesc(eq(shopId), any(Pageable.class));
  }

  @Test
  @DisplayName("존재하지 않는 매장 ID로 조회하면 빈 페이지를 반환한다")
  void 존재하지_않는_매장_ID로_조회하면_빈_페이지를_반환한다() {
    // Given
    Long nonExistentShopId = 999L;
    CampaignSearchRequest request = new CampaignSearchRequest(nonExistentShopId, 0, 10);

    Page<Campaign> emptyCampaignPage = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

    given(
            campaignQueryRepository.findByShopIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                eq(nonExistentShopId), any(Pageable.class)))
        .willReturn(emptyCampaignPage);

    // When
    PagedResult<CampaignQueryResponse> result = campaignQueryService.getCampaignsByShop(request);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getPagination().getTotalItems()).isEqualTo(0);
    assertThat(result.getPagination().getTotalPages()).isEqualTo(0);
    assertThat(result.getPagination().getCurrentPage()).isEqualTo(0);

    then(campaignQueryRepository)
        .should(times(1))
        .findByShopIdAndDeletedAtIsNullOrderByCreatedAtDesc(
            eq(nonExistentShopId), any(Pageable.class));
  }

  @Test
  @DisplayName("페이징 파라미터가 올바르게 적용된다")
  void 페이징_파라미터가_올바르게_적용된다() {
    // Given
    Long shopId = 1L;
    int page = 1;
    int size = 5;
    CampaignSearchRequest request = new CampaignSearchRequest(shopId, page, size);

    Campaign campaign =
        Campaign.builder()
            .id(1L)
            .campaignTitle("페이징 테스트 캠페인")
            .description("페이징 테스트 설명")
            .startDate(LocalDate.now().minusDays(5))
            .endDate(LocalDate.now().plusDays(5))
            .staffId(1L)
            .templateId(1L)
            .couponId(1L)
            .shopId(shopId)
            .createdAt(LocalDateTime.now())
            .build();

    List<Campaign> campaigns = List.of(campaign);
    Page<Campaign> campaignPage = new PageImpl<>(campaigns, PageRequest.of(page, size), 10);

    given(
            campaignQueryRepository.findByShopIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                eq(shopId), any(Pageable.class)))
        .willReturn(campaignPage);

    // When
    PagedResult<CampaignQueryResponse> result = campaignQueryService.getCampaignsByShop(request);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getPagination().getTotalItems()).isEqualTo(10);
    assertThat(result.getPagination().getTotalPages()).isEqualTo(2);
    assertThat(result.getPagination().getCurrentPage()).isEqualTo(page);

    then(campaignQueryRepository)
        .should(times(1))
        .findByShopIdAndDeletedAtIsNullOrderByCreatedAtDesc(eq(shopId), any(Pageable.class));
  }

  @Test
  @DisplayName("삭제된 캠페인은 조회되지 않는다")
  void 삭제된_캠페인은_조회되지_않는다() {
    // Given
    Long shopId = 1L;
    CampaignSearchRequest request = new CampaignSearchRequest(shopId, 0, 10);

    // 삭제되지 않은 캠페인만 조회되도록 Mock 설정
    Campaign activeCampaign =
        Campaign.builder()
            .id(1L)
            .campaignTitle("활성 캠페인")
            .description("활성 캠페인 설명")
            .startDate(LocalDate.now().minusDays(5))
            .endDate(LocalDate.now().plusDays(5))
            .staffId(1L)
            .templateId(1L)
            .couponId(1L)
            .shopId(shopId)
            .createdAt(LocalDateTime.now())
            .build();

    List<Campaign> campaigns = List.of(activeCampaign);
    Page<Campaign> campaignPage = new PageImpl<>(campaigns, PageRequest.of(0, 10), 1);

    given(
            campaignQueryRepository.findByShopIdAndDeletedAtIsNullOrderByCreatedAtDesc(
                eq(shopId), any(Pageable.class)))
        .willReturn(campaignPage);

    // When
    PagedResult<CampaignQueryResponse> result = campaignQueryService.getCampaignsByShop(request);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
    assertThat(result.getContent().get(0).getCampaignTitle()).isEqualTo("활성 캠페인");

    then(campaignQueryRepository)
        .should(times(1))
        .findByShopIdAndDeletedAtIsNullOrderByCreatedAtDesc(eq(shopId), any(Pageable.class));
  }
}
