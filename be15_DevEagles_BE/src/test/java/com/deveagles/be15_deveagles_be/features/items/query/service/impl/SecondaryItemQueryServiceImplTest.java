package com.deveagles.be15_deveagles_be.features.items.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.SecondaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.SecondaryItemRepository;
import com.deveagles.be15_deveagles_be.features.items.query.dto.response.SecondaryItemResponse;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SecondaryItemQueryServiceImplTest {

  @Mock private SecondaryItemRepository secondaryItemRepository;

  @InjectMocks private SecondaryItemQueryServiceImpl secondaryItemQueryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getAllSecondaryItems_shouldReturnMappedResponses() {
    // given
    Long shopId = 1L;

    SecondaryItem item1 =
        SecondaryItem.builder()
            .secondaryItemId(1L)
            .primaryItemId(10L)
            .secondaryItemName("히피펌")
            .secondaryItemPrice(158000)
            .timeTaken(90)
            .isActive(true)
            .build();

    SecondaryItem item2 =
        SecondaryItem.builder()
            .secondaryItemId(2L)
            .primaryItemId(11L)
            .secondaryItemName("볼륨매직")
            .secondaryItemPrice(178000)
            .timeTaken(120)
            .isActive(false)
            .build();

    when(secondaryItemRepository.findAllByShopId(shopId)).thenReturn(Arrays.asList(item1, item2));

    // when
    List<SecondaryItemResponse> result = secondaryItemQueryService.getAllSecondaryItems(shopId);

    // then
    assertThat(result).hasSize(2);

    assertThat(result.get(0).getSecondaryItemId()).isEqualTo(1L);
    assertThat(result.get(0).getSecondaryItemName()).isEqualTo("히피펌");
    assertThat(result.get(0).getSecondaryItemPrice()).isEqualTo(158000);
    assertThat(result.get(0).getTimeTaken()).isEqualTo(90);
    assertThat(result.get(0).isActive()).isTrue();

    assertThat(result.get(1).getSecondaryItemId()).isEqualTo(2L);
    assertThat(result.get(1).getSecondaryItemName()).isEqualTo("볼륨매직");
    assertThat(result.get(1).getSecondaryItemPrice()).isEqualTo(178000);
    assertThat(result.get(1).getTimeTaken()).isEqualTo(120);
    assertThat(result.get(1).isActive()).isFalse();

    verify(secondaryItemRepository, times(1)).findAllByShopId(shopId);
  }
}
