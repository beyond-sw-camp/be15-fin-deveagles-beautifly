package com.deveagles.be15_deveagles_be.features.membership.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerExpiringPrepaidPassFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerExpiringPrepaidPassResponse;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerExpiringPrepaidPassResult;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.PrepaidPassInfo;
import com.deveagles.be15_deveagles_be.features.membership.query.mapper.CustomerMembershipMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CustomerExpiringPrepaidPassQueryServiceImplTest {

  @Mock private CustomerMembershipMapper customerMembershipMapper;

  @InjectMocks private CustomerExpiringPrepaidPassQueryServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("서비스 로직 - 만료 예정 선불권 고객 목록 정상 반환")
  void getExpiringPrepaidPasses_withMockedDTOs() {
    // given
    Long shopId = 1L;

    // mock 필터 요청 객체
    CustomerExpiringPrepaidPassFilterRequest request =
        mock(CustomerExpiringPrepaidPassFilterRequest.class);
    when(request.getPage()).thenReturn(1);
    when(request.getSize()).thenReturn(10);
    when(request.getOffset()).thenReturn(0);

    // mock DTO 응답 객체들
    CustomerExpiringPrepaidPassResponse customerMock =
        mock(CustomerExpiringPrepaidPassResponse.class);
    PrepaidPassInfo passMock = mock(PrepaidPassInfo.class);

    // stub mapper
    when(customerMembershipMapper.findExpiringPrepaidPassCustomers(shopId, request, 0))
        .thenReturn(List.of(customerMock));
    when(customerMembershipMapper.findExpiringPrepaidPassesByCustomerId(anyLong()))
        .thenReturn(List.of(passMock));
    when(customerMembershipMapper.countExpiringPrepaidPassCustomers(shopId, request))
        .thenReturn(1L);

    // when
    CustomerExpiringPrepaidPassResult result = service.getExpiringPrepaidPasses(shopId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getList()).hasSize(1);
    assertThat(result.getPagination().getTotalItems()).isEqualTo(1L);
    assertThat(result.getPagination().getCurrentPage()).isEqualTo(1);
    assertThat(result.getPagination().getTotalPages()).isEqualTo(1);
  }
}
