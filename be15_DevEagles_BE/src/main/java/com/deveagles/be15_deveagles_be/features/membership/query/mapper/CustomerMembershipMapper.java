package com.deveagles.be15_deveagles_be.features.membership.query.mapper;

import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerMemebershipFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerMembershipResponse;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.SessionPassInfo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMembershipMapper {
  // 전체 조회용
  List<CustomerMembershipResponse> findAllCustomerMemberships(Long shopId, int offset, int size);

  long countAllCustomerMemberships(Long shopId);

  List<SessionPassInfo> findSessionPassesByCustomerId(Long customerId);

  // 필터 조회용
  List<CustomerMembershipResponse> findCustomerMemberships(
      Long shopId, CustomerMemebershipFilterRequest filter, int offset);

  long countCustomerMemberships(Long shopId, CustomerMemebershipFilterRequest filter);
}
