package com.deveagles.be15_deveagles_be.features.membership.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.PrepaidPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.PrepaidPassRepository;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.PrepaidPassResponse;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PrepaidPassQueryServiceImplTest {

  @Mock private PrepaidPassRepository prepaidPassRepository;

  @InjectMocks private PrepaidPassQueryServiceImpl prepaidPassQueryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("성공: 전체 선불권 목록을 조회한다")
  void getAllPrepaidPasses_shouldReturnBeautyExamples() {
    // given
    PrepaidPass pass1 =
        PrepaidPass.builder()
            .prepaidPassId(1L)
            .prepaidPassName("50만원권")
            .prepaidPassPrice(500000)
            .expirationPeriod(365)
            .bonus(50000)
            .discountRate(0)
            .prepaidPassMemo("")
            .build();

    PrepaidPass pass2 =
        PrepaidPass.builder()
            .prepaidPassId(2L)
            .prepaidPassName("100만원권")
            .prepaidPassPrice(1000000)
            .expirationPeriod(365)
            .bonus(100000)
            .discountRate(5)
            .prepaidPassMemo("100만원권")
            .build();

    when(prepaidPassRepository.findAllByDeletedAtIsNull()).thenReturn(Arrays.asList(pass1, pass2));

    // when
    List<PrepaidPassResponse> result = prepaidPassQueryService.getAllPrepaidPass();

    // then
    assertThat(result).hasSize(2);

    assertThat(result.get(0).getPrepaidPassId()).isEqualTo(1L);
    assertThat(result.get(0).getPrepaidPassName()).isEqualTo("50만원권");
    assertThat(result.get(0).getPrepaidPassPrice()).isEqualTo(500000);
    assertThat(result.get(0).getType()).isEqualTo("PREPAID");

    assertThat(result.get(1).getPrepaidPassId()).isEqualTo(2L);
    assertThat(result.get(1).getPrepaidPassName()).isEqualTo("100만원권");
    assertThat(result.get(1).getPrepaidPassPrice()).isEqualTo(1000000);
    assertThat(result.get(1).getType()).isEqualTo("PREPAID");

    verify(prepaidPassRepository, times(1)).findAllByDeletedAtIsNull();
  }
}
