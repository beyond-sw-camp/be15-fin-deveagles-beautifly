package com.deveagles.be15_deveagles_be.features.items.query.service.impl;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.PrimaryItemRepository;
import com.deveagles.be15_deveagles_be.features.items.query.dto.response.PrimaryItemResponse;
import com.deveagles.be15_deveagles_be.features.items.query.service.PrimaryItemQueryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriamryItemQueryServiceImpl implements PrimaryItemQueryService {

  private final PrimaryItemRepository primaryItemRepository;

  @Override
  public List<PrimaryItemResponse> getAllPrimaryItems() {
    List<PrimaryItem> items = primaryItemRepository.findAll();
    return items.stream().map(PrimaryItemResponse::from).collect(Collectors.toList());
  }
}
