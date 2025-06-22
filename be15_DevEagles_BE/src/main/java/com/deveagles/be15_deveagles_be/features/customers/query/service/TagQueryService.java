package com.deveagles.be15_deveagles_be.features.customers.query.service;

import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.TagResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagQueryService {

  TagResponse getTag(Long tagId);

  List<TagResponse> getAllTags();

  Page<TagResponse> getTags(Pageable pageable);
}
