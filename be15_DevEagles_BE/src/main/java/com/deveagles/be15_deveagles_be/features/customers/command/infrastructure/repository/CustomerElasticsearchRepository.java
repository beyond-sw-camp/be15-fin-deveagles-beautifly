package com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerDocument;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CustomerElasticsearchRepository
    extends ElasticsearchRepository<CustomerDocument, String> {

  Page<CustomerDocument> findByShopIdAndDeletedAtIsNull(Long shopId, Pageable pageable);

  // 이름 또는 전화번호로 검색
  @Query(
      """
        {
          "bool": {
            "must": [
              {"term": {"shopId": "?0"}},
              {"bool": {"must_not": {"exists": {"field": "deletedAt"}}}}
            ],
            "should": [
              {"match": {"customerName": {"query": "?1", "fuzziness": "AUTO"}}},
              {"wildcard": {"customerName.keyword": "*?1*"}},
              {"prefix": {"phoneNumber": "?1"}},
              {"wildcard": {"phoneNumber": "*?1*"}}
            ],
            "minimum_should_match": 1
          }
        }
        """)
  List<CustomerDocument> searchByNameOrPhoneNumber(Long shopId, String keyword);

  @Query(
      """
        {
          "bool": {
            "must": [
              {"term": {"shopId": "?0"}},
              {"bool": {"must_not": {"exists": {"field": "deletedAt"}}}}
            ],
            "should": [
              {"multi_match": {
                "query": "?1",
                "fields": ["customerName^2", "phoneNumber"],
                "type": "best_fields",
                "fuzziness": "AUTO"
              }},
              {"prefix": {"customerName.keyword": "?1"}},
              {"prefix": {"phoneNumber": "?1"}}
            ],
            "minimum_should_match": 1
          }
        }
        """)
  Page<CustomerDocument> advancedSearch(Long shopId, String keyword, Pageable pageable);

  // 자동완성용 검색
  @Query(
      """
        {
          "bool": {
            "must": [
              {"term": {"shopId": "?0"}},
              {"bool": {"must_not": {"exists": {"field": "deletedAt"}}}}
            ],
            "should": [
              {"prefix": {"customerName.keyword": "?1"}},
              {"prefix": {"phoneNumber": "?1"}}
            ],
            "minimum_should_match": 1
          }
        }
        """)
  List<CustomerDocument> autocomplete(Long shopId, String prefix);
}
