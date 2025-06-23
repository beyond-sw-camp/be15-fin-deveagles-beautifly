package com.deveagles.be15_deveagles_be.features.customers.query.service;

import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.TagResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagQueryService {

  TagResponse getTag(Long tagId, Long shopId);

  List<TagResponse> getAllTags();

  List<TagResponse> getAllTagsByShopId(Long shopId);

  Page<TagResponse> getTags(Pageable pageable);
}
