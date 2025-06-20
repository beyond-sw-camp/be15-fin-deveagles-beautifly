package com.deveagles.be15_deveagles_be.features.customers.command.application.service;

import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateTagRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateTagRequest;

public interface TagCommandService {

  Long createTag(CreateTagRequest request);

  void updateTag(Long tagId, UpdateTagRequest request);

  void deleteTag(Long tagId);
}
