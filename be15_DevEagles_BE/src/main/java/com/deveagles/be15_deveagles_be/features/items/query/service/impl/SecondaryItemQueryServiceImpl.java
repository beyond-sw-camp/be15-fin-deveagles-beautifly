package com.deveagles.be15_deveagles_be.features.items.query.service.impl;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.SecondaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.SecondaryItemRepository;
import com.deveagles.be15_deveagles_be.features.items.query.dto.response.SecondaryItemResponse;
import com.deveagles.be15_deveagles_be.features.items.query.service.SecondaryItemQueryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecondaryItemQueryServiceImpl implements SecondaryItemQueryService {

  private final SecondaryItemRepository secondaryItemRepository;

  @Override
  public List<SecondaryItemResponse> getAllSecondaryItems() {
    List<SecondaryItem> items = secondaryItemRepository.findAllByDeletedAtIsNull();
    return items.stream().map(SecondaryItemResponse::from).collect(Collectors.toList());
  }
}
