package com.deveagles.be15_deveagles_be.features.customers.query.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.CustomerSearchQuery;
import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerSearchResult;
import java.util.List;

public interface CustomerSearchService {

  // 이름 또는 전화번호로 검색 (Elasticsearch 사용)
  List<CustomerSearchResult> searchByKeyword(String keyword, Long shopId);

  // 고급 검색 (페이징 포함)
  PagedResult<CustomerSearchResult> advancedSearch(CustomerSearchQuery query);

  // 자동완성
  List<String> autocomplete(String prefix, Long shopId);

  // 검색 결과 개수
  long countByKeyword(String keyword, Long shopId);

  // Elasticsearch 인덱스 동기화
  void syncCustomerToElasticsearch(Long customerId);

  // 전체 고객 데이터 재인덱싱
  void reindexAllCustomers(Long shopId);
}
