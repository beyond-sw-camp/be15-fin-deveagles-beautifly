package com.deveagles.be15_deveagles_be.features.items.query.service;

import com.deveagles.be15_deveagles_be.features.items.query.dto.response.PrimaryItemResponse;
import java.util.List;

public interface PrimaryItemQueryService {
  List<PrimaryItemResponse> getAllPrimaryItems();
}
