package com.deveagles.be15_deveagles_be.features.membership.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerExpiringSessionPassFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerExpiringSessionPassResponse;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerExpiringSessionPassResult;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.SessionPassInfo;
import com.deveagles.be15_deveagles_be.features.membership.query.mapper.CustomerMembershipMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CustomerExpiringSessionPassQueryServiceImplTest {

  @Mock private CustomerMembershipMapper customerMembershipMapper;

  @InjectMocks private CustomerExpiringSessionPassQueryServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("서비스 로직 - 만료 예정 횟수권 고객 목록 정상 반환")
  void getExpiringSessionPasses_withMockedDTOs() {
    // given
    Long shopId = 1L;

    // mock 요청 DTO
    CustomerExpiringSessionPassFilterRequest request =
        mock(CustomerExpiringSessionPassFilterRequest.class);
    when(request.getPage()).thenReturn(1);
    when(request.getSize()).thenReturn(10);
    when(request.getOffset()).thenReturn(0);

    // mock 응답 DTO
    CustomerExpiringSessionPassResponse customerMock =
        mock(CustomerExpiringSessionPassResponse.class);
    SessionPassInfo sessionPassMock = mock(SessionPassInfo.class);

    when(customerMock.getCustomerId()).thenReturn(100L);

    // stub mapper
    when(customerMembershipMapper.findExpiringSessionPassCustomers(shopId, request, 0))
        .thenReturn(List.of(customerMock));
    when(customerMembershipMapper.findExpiringSessionPassesByCustomerId(100L))
        .thenReturn(List.of(sessionPassMock));
    when(customerMembershipMapper.countExpiringSessionPassCustomers(shopId, request))
        .thenReturn(1L);

    // when
    CustomerExpiringSessionPassResult result = service.getExpiringSessionPasses(shopId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getList()).hasSize(1);
    assertThat(result.getPagination().getTotalItems()).isEqualTo(1);
    assertThat(result.getPagination().getCurrentPage()).isEqualTo(1);
    assertThat(result.getPagination().getTotalPages()).isEqualTo(1);

    // verify mapper calls
    verify(customerMembershipMapper).findExpiringSessionPassCustomers(shopId, request, 0);
    verify(customerMembershipMapper).findExpiringSessionPassesByCustomerId(100L);
    verify(customerMembershipMapper).countExpiringSessionPassCustomers(shopId, request);
  }
}
