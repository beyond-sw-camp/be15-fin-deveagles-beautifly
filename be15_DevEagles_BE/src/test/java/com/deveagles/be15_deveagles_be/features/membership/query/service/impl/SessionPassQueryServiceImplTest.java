package com.deveagles.be15_deveagles_be.features.membership.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.SessionPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.SessionPassRepository;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.SessionPassResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SessionPassQueryServiceImplTest {

  @Mock private SessionPassRepository sessionPassRepository;

  @InjectMocks private SessionPassQueryServiceImpl sessionPassQueryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("성공: 전체 횟수권 목록을 조회한다")
  void getAllSessionPasses_shouldReturnSessionExamples() {
    // given
    Shop shop = Shop.builder().shopId(1L).build();

    SessionPass pass1 =
        SessionPass.builder()
            .sessionPassId(1L)
            .sessionPassName("스킨케어 5회권")
            .sessionPassPrice(120000)
            .session(5)
            .expirationPeriod(90)
            .bonus(1)
            .shopId(shop)
            .discountRate(5)
            .sessionPassMemo("피부 집중 관리")
            .build();

    SessionPass pass2 =
        SessionPass.builder()
            .sessionPassId(2L)
            .sessionPassName("두피 관리 10회권")
            .sessionPassPrice(220000)
            .session(10)
            .expirationPeriod(180)
            .bonus(2)
            .shopId(shop)
            .discountRate(8)
            .sessionPassMemo("탈모 예방 집중 케어")
            .build();

    when(sessionPassRepository.findAllByShopId_ShopIdAndDeletedAtIsNull(1L))
        .thenReturn(Arrays.asList(pass1, pass2));

    // when
    List<SessionPassResponse> result = sessionPassQueryService.getAllSessionPass(1L);

    // then
    assertThat(result).hasSize(2);

    assertThat(result.get(0).getSessionPassId()).isEqualTo(1L);
    assertThat(result.get(0).getSessionPassName()).isEqualTo("스킨케어 5회권");
    assertThat(result.get(0).getSession()).isEqualTo(5);
    assertThat(result.get(0).getType()).isEqualTo("SESSION");

    assertThat(result.get(1).getSessionPassId()).isEqualTo(2L);
    assertThat(result.get(1).getSessionPassName()).isEqualTo("두피 관리 10회권");
    assertThat(result.get(1).getSession()).isEqualTo(10);
    assertThat(result.get(1).getType()).isEqualTo("SESSION");

    verify(sessionPassRepository, times(1)).findAllByShopId_ShopIdAndDeletedAtIsNull(1L);
  }
}
