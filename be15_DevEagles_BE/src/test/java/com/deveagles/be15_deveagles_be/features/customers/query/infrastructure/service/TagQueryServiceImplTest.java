package com.deveagles.be15_deveagles_be.features.customers.query.infrastructure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.TagResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Tag;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.TagRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("태그 쿼리 서비스 테스트")
class TagQueryServiceImplTest {

  @Mock private TagRepository tagRepository;

  @InjectMocks private TagQueryServiceImpl tagQueryService;

  @Test
  @DisplayName("태그 단건 조회 성공")
  void getTag_Success() {
    // given
    Long tagId = 1L;
    Tag tag = createTestTag(tagId, "VIP고객", "#FF0000");

    given(tagRepository.findById(tagId)).willReturn(Optional.of(tag));

    // when
    TagResponse response = tagQueryService.getTag(tagId);

    // then
    assertThat(response.getTagId()).isEqualTo(tagId);
    assertThat(response.getTagName()).isEqualTo("VIP고객");
    assertThat(response.getColorCode()).isEqualTo("#FF0000");

    then(tagRepository).should().findById(tagId);
  }

  @Test
  @DisplayName("태그 단건 조회 실패 - 존재하지 않는 태그")
  void getTag_NotFound() {
    // given
    Long tagId = 1L;
    given(tagRepository.findById(tagId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> tagQueryService.getTag(tagId))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("태그를 찾을 수 없습니다.");

    then(tagRepository).should().findById(tagId);
  }

  @Test
  @DisplayName("전체 태그 목록 조회 성공")
  void getAllTags_Success() {
    // given
    List<Tag> tags =
        List.of(
            createTestTag(1L, "VIP고객", "#FF0000"),
            createTestTag(2L, "단골고객", "#00FF00"),
            createTestTag(3L, "신규고객", "#0000FF"));

    given(tagRepository.findAll()).willReturn(tags);

    // when
    List<TagResponse> responses = tagQueryService.getAllTags();

    // then
    assertThat(responses).hasSize(3);
    assertThat(responses.get(0).getTagName()).isEqualTo("VIP고객");
    assertThat(responses.get(0).getColorCode()).isEqualTo("#FF0000");
    assertThat(responses.get(1).getTagName()).isEqualTo("단골고객");
    assertThat(responses.get(1).getColorCode()).isEqualTo("#00FF00");
    assertThat(responses.get(2).getTagName()).isEqualTo("신규고객");
    assertThat(responses.get(2).getColorCode()).isEqualTo("#0000FF");

    then(tagRepository).should().findAll();
  }

  @Test
  @DisplayName("전체 태그 목록 조회 - 빈 목록")
  void getAllTags_EmptyList() {
    // given
    given(tagRepository.findAll()).willReturn(List.of());

    // when
    List<TagResponse> responses = tagQueryService.getAllTags();

    // then
    assertThat(responses).isEmpty();

    then(tagRepository).should().findAll();
  }

  @Test
  @DisplayName("태그 페이징 조회 성공")
  void getTags_Success() {
    // given
    Pageable pageable = PageRequest.of(0, 2);
    List<Tag> tags =
        List.of(createTestTag(1L, "VIP고객", "#FF0000"), createTestTag(2L, "단골고객", "#00FF00"));
    Page<Tag> tagPage = new PageImpl<>(tags, pageable, 3);

    given(tagRepository.findAll(pageable)).willReturn(tagPage);

    // when
    Page<TagResponse> responses = tagQueryService.getTags(pageable);

    // then
    assertThat(responses.getContent()).hasSize(2);
    assertThat(responses.getTotalElements()).isEqualTo(3);
    assertThat(responses.getTotalPages()).isEqualTo(2);
    assertThat(responses.getNumber()).isEqualTo(0);
    assertThat(responses.getSize()).isEqualTo(2);

    assertThat(responses.getContent().get(0).getTagName()).isEqualTo("VIP고객");
    assertThat(responses.getContent().get(0).getColorCode()).isEqualTo("#FF0000");
    assertThat(responses.getContent().get(1).getTagName()).isEqualTo("단골고객");
    assertThat(responses.getContent().get(1).getColorCode()).isEqualTo("#00FF00");

    then(tagRepository).should().findAll(pageable);
  }

  @Test
  @DisplayName("태그 페이징 조회 - 빈 페이지")
  void getTags_EmptyPage() {
    // given
    Pageable pageable = PageRequest.of(0, 10);
    Page<Tag> emptyPage = new PageImpl<>(List.of(), pageable, 0);

    given(tagRepository.findAll(pageable)).willReturn(emptyPage);

    // when
    Page<TagResponse> responses = tagQueryService.getTags(pageable);

    // then
    assertThat(responses.getContent()).isEmpty();
    assertThat(responses.getTotalElements()).isEqualTo(0);
    assertThat(responses.getTotalPages()).isEqualTo(0);

    then(tagRepository).should().findAll(pageable);
  }

  private Tag createTestTag(Long id, String tagName, String colorCode) {
    return Tag.builder().id(id).tagName(tagName).colorCode(colorCode).build();
  }
}
