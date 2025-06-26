package com.deveagles.be15_deveagles_be.features.items.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.Category;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.PrimaryItemRepository;
import com.deveagles.be15_deveagles_be.features.items.query.dto.response.PrimaryItemResponse;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PrimaryItemQueryServiceImplTest {

  @Mock private PrimaryItemRepository primaryItemRepository;

  @InjectMocks private PriamryItemQueryServiceImpl primaryItemQueryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getAllPrimaryItems_shouldReturnMappedResponses() {
    // given
    PrimaryItem item1 =
        PrimaryItem.builder()
            .primaryItemId(1L)
            .primaryItemName("헤어컷")
            .category(Category.valueOf("SERVICE"))
            .build();

    PrimaryItem item2 =
        PrimaryItem.builder()
            .primaryItemId(2L)
            .primaryItemName("샴푸")
            .category(Category.valueOf("PRODUCT"))
            .build();

    when(primaryItemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

    // when
    List<PrimaryItemResponse> result = primaryItemQueryService.getAllPrimaryItems();

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).getPrimaryItemId()).isEqualTo(1L);
    assertThat(result.get(0).getPrimaryItemName()).isEqualTo("헤어컷");
    assertThat(result.get(0).getCategory()).isEqualTo(Category.SERVICE);

    verify(primaryItemRepository, times(1)).findAll();
  }
}
