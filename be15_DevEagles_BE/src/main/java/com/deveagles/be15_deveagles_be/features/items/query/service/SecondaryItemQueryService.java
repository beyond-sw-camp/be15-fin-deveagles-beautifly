package com.deveagles.be15_deveagles_be.features.items.query.service;

import com.deveagles.be15_deveagles_be.features.items.query.dto.response.SecondaryItemResponse;
import java.util.List;

public interface SecondaryItemQueryService {
  List<SecondaryItemResponse> getAllSecondaryItems();
}
