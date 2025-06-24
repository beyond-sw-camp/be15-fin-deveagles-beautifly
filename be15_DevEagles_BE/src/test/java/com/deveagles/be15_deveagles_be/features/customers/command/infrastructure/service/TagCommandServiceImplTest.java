package com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateTagRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateTagRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Tag;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.TagRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("태그 커맨드 서비스 테스트")
class TagCommandServiceImplTest {

  @Mock private TagRepository tagRepository;

  @InjectMocks private TagCommandServiceImpl tagCommandService;

  @Test
  @DisplayName("태그 생성 성공")
  void createTag_Success() {
    // given
    String tagName = "VIP고객";
    String colorCode = "#FF0000";
    Long shopId = 1L;
    CreateTagRequest request = new CreateTagRequest(shopId, tagName, colorCode);

    Tag savedTag =
        Tag.builder().id(1L).shopId(shopId).tagName(tagName).colorCode(colorCode).build();

    given(tagRepository.existsByTagNameAndShopId(tagName, shopId)).willReturn(false);
    given(tagRepository.save(any(Tag.class))).willReturn(savedTag);

    // when
    Long result = tagCommandService.createTag(request);

    // then
    assertThat(result).isEqualTo(1L);

    then(tagRepository).should().existsByTagNameAndShopId(tagName, shopId);
    then(tagRepository).should().save(any(Tag.class));
  }

  @Test
  @DisplayName("태그 생성 실패 - 중복된 태그명")
  void createTag_DuplicateTagName() {
    // given
    String tagName = "VIP고객";
    String colorCode = "#FF0000";
    Long shopId = 1L;
    CreateTagRequest request = new CreateTagRequest(shopId, tagName, colorCode);

    given(tagRepository.existsByTagNameAndShopId(tagName, shopId)).willReturn(true);

    // when & then
    assertThatThrownBy(() -> tagCommandService.createTag(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("이미 존재하는 태그명입니다.");

    then(tagRepository).should().existsByTagNameAndShopId(tagName, shopId);
    then(tagRepository).should(never()).save(any(Tag.class));
  }

  @Test
  @DisplayName("태그 수정 성공")
  void updateTag_Success() {
    // given
    Long tagId = 1L;
    Long shopId = 1L;
    String oldTagName = "VIP고객";
    String oldColorCode = "#FF0000";
    String newTagName = "VVIP고객";
    String newColorCode = "#FFD700";
    UpdateTagRequest request = new UpdateTagRequest(shopId, newTagName, newColorCode);

    Tag existingTag =
        Tag.builder().id(tagId).shopId(shopId).tagName(oldTagName).colorCode(oldColorCode).build();

    given(tagRepository.findByIdAndShopId(tagId, shopId)).willReturn(Optional.of(existingTag));
    given(tagRepository.existsByTagNameAndShopId(newTagName, shopId)).willReturn(false);

    // when
    tagCommandService.updateTag(tagId, request);

    // then
    then(tagRepository).should().findByIdAndShopId(tagId, shopId);
    then(tagRepository).should().existsByTagNameAndShopId(newTagName, shopId);
  }

  @Test
  @DisplayName("태그 수정 실패 - 존재하지 않는 태그")
  void updateTag_TagNotFound() {
    // given
    Long tagId = 1L;
    Long shopId = 1L;
    UpdateTagRequest request = new UpdateTagRequest(shopId, "VVIP고객", "#FFD700");

    given(tagRepository.findByIdAndShopId(tagId, shopId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> tagCommandService.updateTag(tagId, request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("태그를 찾을 수 없습니다.");

    then(tagRepository).should().findByIdAndShopId(tagId, shopId);
    then(tagRepository).should(never()).existsByTagNameAndShopId(any(), any());
  }

  @Test
  @DisplayName("태그 수정 실패 - 중복된 태그명")
  void updateTag_DuplicateTagName() {
    // given
    Long tagId = 1L;
    Long shopId = 1L;
    String oldTagName = "VIP고객";
    String oldColorCode = "#FF0000";
    String newTagName = "VVIP고객";
    String newColorCode = "#FFD700";
    UpdateTagRequest request = new UpdateTagRequest(shopId, newTagName, newColorCode);

    Tag existingTag =
        Tag.builder().id(tagId).shopId(shopId).tagName(oldTagName).colorCode(oldColorCode).build();

    given(tagRepository.findByIdAndShopId(tagId, shopId)).willReturn(Optional.of(existingTag));
    given(tagRepository.existsByTagNameAndShopId(newTagName, shopId)).willReturn(true);

    // when & then
    assertThatThrownBy(() -> tagCommandService.updateTag(tagId, request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("이미 존재하는 태그명입니다.");

    then(tagRepository).should().findByIdAndShopId(tagId, shopId);
    then(tagRepository).should().existsByTagNameAndShopId(newTagName, shopId);
  }

  @Test
  @DisplayName("태그 수정 성공 - 동일한 태그명으로 수정시 중복 검사 스킵")
  void updateTag_SameTagNameSkipDuplicateCheck() {
    // given
    Long tagId = 1L;
    Long shopId = 1L;
    String tagName = "VIP고객";
    String oldColorCode = "#FF0000";
    String newColorCode = "#FFD700";
    UpdateTagRequest request = new UpdateTagRequest(shopId, tagName, newColorCode);

    Tag existingTag =
        Tag.builder().id(tagId).shopId(shopId).tagName(tagName).colorCode(oldColorCode).build();

    given(tagRepository.findByIdAndShopId(tagId, shopId)).willReturn(Optional.of(existingTag));

    // when
    tagCommandService.updateTag(tagId, request);

    // then
    then(tagRepository).should().findByIdAndShopId(tagId, shopId);
    then(tagRepository).should(never()).existsByTagNameAndShopId(any(), any());
  }

  @Test
  @DisplayName("태그 삭제 성공")
  void deleteTag_Success() {
    // given
    Long tagId = 1L;
    Tag existingTag =
        Tag.builder().id(tagId).shopId(1L).tagName("VIP고객").colorCode("#FF0000").build();

    given(tagRepository.findById(tagId)).willReturn(Optional.of(existingTag));

    // when
    tagCommandService.deleteTag(tagId);

    // then
    then(tagRepository).should().findById(tagId);
    then(tagRepository).should().delete(existingTag);
  }

  @Test
  @DisplayName("태그 삭제 실패 - 존재하지 않는 태그")
  void deleteTag_TagNotFound() {
    // given
    Long tagId = 1L;
    given(tagRepository.findById(tagId)).willReturn(Optional.empty());

    // when & then
    assertThatThrownBy(() -> tagCommandService.deleteTag(tagId))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("태그를 찾을 수 없습니다.");

    then(tagRepository).should().findById(tagId);
    then(tagRepository).should(never()).delete(any());
  }
}
