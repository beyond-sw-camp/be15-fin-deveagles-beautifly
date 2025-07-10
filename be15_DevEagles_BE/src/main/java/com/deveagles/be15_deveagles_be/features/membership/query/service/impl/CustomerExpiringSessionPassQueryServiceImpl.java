package com.deveagles.be15_deveagles_be.features.membership.query.service.impl;

import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerExpiringSessionPassFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerExpiringSessionPassResponse;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerExpiringSessionPassResult;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.SessionPassInfo;
import com.deveagles.be15_deveagles_be.features.membership.query.mapper.CustomerMembershipMapper;
import com.deveagles.be15_deveagles_be.features.membership.query.service.CustomerExpiringSessionPassQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerExpiringSessionPassQueryServiceImpl
    implements CustomerExpiringSessionPassQueryService {

  private final CustomerMembershipMapper customerMembershipMapper;

  @Override
  public CustomerExpiringSessionPassResult getExpiringSessionPasses(
      Long shopId, CustomerExpiringSessionPassFilterRequest request) {

    int offset = request.getOffset();

    List<CustomerExpiringSessionPassResponse> list =
        customerMembershipMapper.findExpiringSessionPassCustomers(shopId, request, offset);

    for (CustomerExpiringSessionPassResponse customer : list) {
      List<SessionPassInfo> sessionPasses =
          customerMembershipMapper.findExpiringSessionPassesByCustomerId(customer.getCustomerId());
      customer.setSessionPasses(sessionPasses);
    }

    long totalItems = customerMembershipMapper.countExpiringSessionPassCustomers(shopId, request);

    Pagination pagination =
        Pagination.builder()
            .currentPage(request.getPage())
            .totalPages((int) Math.ceil((double) totalItems / request.getSize()))
            .totalItems(totalItems)
            .build();

    return CustomerExpiringSessionPassResult.builder().list(list).pagination(pagination).build();
  }
}
