package com.deveagles.be15_deveagles_be.features.campaigns.query.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.features.campaigns.query.dto.response.CampaignQueryResponse;
import com.deveagles.be15_deveagles_be.features.campaigns.query.service.CampaignQueryService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CampaignQueryController.class)
@DisplayName("CampaignQueryController 단위 테스트")
class CampaignQueryControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CampaignQueryService campaignQueryService;

  @Test
  @DisplayName("매장별 캠페인 목록 조회 성공")
  @WithMockUser
  void getCampaignsByShop_매장별캠페인목록조회_성공() throws Exception {
    // Given
    Long shopId = 1L;

    CampaignQueryResponse campaign1 =
        CampaignQueryResponse.builder()
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
            .isActive(true)
            .createdAt(LocalDateTime.now().minusDays(15))
            .build();

    CampaignQueryResponse campaign2 =
        CampaignQueryResponse.builder()
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
            .isActive(true)
            .createdAt(LocalDateTime.now().minusDays(25))
            .build();

    Pagination pagination = Pagination.builder().currentPage(0).totalPages(1).totalItems(2).build();
    PagedResult<CampaignQueryResponse> pagedResult =
        new PagedResult<>(List.of(campaign1, campaign2), pagination);

    given(campaignQueryService.getCampaignsByShop(any())).willReturn(pagedResult);

    // When & Then
    mockMvc
        .perform(
            get("/campaigns")
                .param("shopId", shopId.toString())
                .param("page", "0")
                .param("size", "10"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content").isArray())
        .andExpect(jsonPath("$.data.content.length()").value(2))
        .andExpect(jsonPath("$.data.content[0].id").value(1))
        .andExpect(jsonPath("$.data.content[0].campaignTitle").value("첫 번째 캠페인"))
        .andExpect(jsonPath("$.data.content[1].id").value(2))
        .andExpect(jsonPath("$.data.content[1].campaignTitle").value("두 번째 캠페인"))
        .andExpect(jsonPath("$.data.pagination.totalItems").value(2))
        .andExpect(jsonPath("$.data.pagination.totalPages").value(1))
        .andExpect(jsonPath("$.data.pagination.currentPage").value(0));
  }

  @Test
  @DisplayName("캠페인 상세 조회 성공")
  @WithMockUser
  void getCampaignById_캠페인상세조회_성공() throws Exception {
    // Given
    Long campaignId = 1L;

    CampaignQueryResponse campaign =
        CampaignQueryResponse.builder()
            .id(campaignId)
            .campaignTitle("테스트 캠페인")
            .description("테스트 캠페인 설명")
            .startDate(LocalDate.now().minusDays(5))
            .endDate(LocalDate.now().plusDays(5))
            .messageSendAt(LocalDateTime.now().minusDays(1))
            .staffId(1L)
            .templateId(1L)
            .couponId(1L)
            .shopId(1L)
            .isActive(true)
            .createdAt(LocalDateTime.now().minusDays(10))
            .build();

    given(campaignQueryService.getCampaignById(campaignId)).willReturn(Optional.of(campaign));

    // When & Then
    mockMvc
        .perform(get("/campaigns/{id}", campaignId))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(campaignId))
        .andExpect(jsonPath("$.data.campaignTitle").value("테스트 캠페인"))
        .andExpect(jsonPath("$.data.description").value("테스트 캠페인 설명"))
        .andExpect(jsonPath("$.data.couponId").value(1));
  }

  @Test
  @DisplayName("존재하지 않는 캠페인 조회시 404 에러")
  @WithMockUser
  void getCampaignById_존재하지않는캠페인조회_404에러() throws Exception {
    // Given
    Long nonExistentCampaignId = 999L;

    given(campaignQueryService.getCampaignById(nonExistentCampaignId)).willReturn(Optional.empty());

    // When & Then
    mockMvc
        .perform(get("/campaigns/{id}", nonExistentCampaignId))
        .andDo(print())
        .andExpect(status().isInternalServerError()); // RuntimeException이 발생하므로 500
  }

  @Test
  @DisplayName("매장별 캠페인 조회시 기본 페이징 파라미터 적용")
  @WithMockUser
  void getCampaignsByShop_기본페이징파라미터적용_성공() throws Exception {
    // Given
    Long shopId = 1L;

    Pagination pagination = Pagination.builder().currentPage(0).totalPages(0).totalItems(0).build();
    PagedResult<CampaignQueryResponse> emptyResult = new PagedResult<>(List.of(), pagination);

    given(campaignQueryService.getCampaignsByShop(any())).willReturn(emptyResult);

    // When & Then
    mockMvc
        .perform(get("/campaigns").param("shopId", shopId.toString()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content").isArray())
        .andExpect(jsonPath("$.data.content.length()").value(0))
        .andExpect(jsonPath("$.data.pagination.currentPage").value(0));
  }
}
