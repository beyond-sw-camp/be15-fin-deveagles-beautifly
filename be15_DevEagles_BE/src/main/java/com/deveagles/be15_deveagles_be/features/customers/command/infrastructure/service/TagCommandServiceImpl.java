package com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateTagRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateTagRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.service.TagCommandService;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Tag;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TagCommandServiceImpl implements TagCommandService {

  private final TagRepository tagRepository;

  @Override
  public Long createTag(CreateTagRequest request) {
    log.info("태그 생성 요청 - 태그명: {}, 색상코드: {}", request.getTagName(), request.getColorCode());

    validateTagNameNotExists(request.getTagName());

    Tag tag = Tag.builder().tagName(request.getTagName()).colorCode(request.getColorCode()).build();

    Tag savedTag = tagRepository.save(tag);

    log.info(
        "태그 생성 완료 - ID: {}, 태그명: {}, 색상코드: {}",
        savedTag.getId(),
        savedTag.getTagName(),
        savedTag.getColorCode());
    return savedTag.getId();
  }

  @Override
  public void updateTag(Long tagId, UpdateTagRequest request) {
    log.info(
        "태그 수정 요청 - ID: {}, 새 태그명: {}, 새 색상코드: {}",
        tagId,
        request.getTagName(),
        request.getColorCode());

    Tag tag = findTagById(tagId);

    if (!tag.getTagName().equals(request.getTagName())) {
      validateTagNameNotExists(request.getTagName());
    }

    String oldTagName = tag.getTagName();
    String oldColorCode = tag.getColorCode();

    tag.updateTagInfo(request.getTagName(), request.getColorCode());

    log.info(
        "태그 수정 완료 - ID: {}, 태그명: {} -> {}, 색상코드: {} -> {}",
        tagId,
        oldTagName,
        request.getTagName(),
        oldColorCode,
        request.getColorCode());
  }

  @Override
  public void deleteTag(Long tagId) {
    log.info("태그 삭제 요청 - ID: {}", tagId);

    Tag tag = findTagById(tagId);
    tagRepository.delete(tag);

    log.info("태그 삭제 완료 - ID: {}, 태그명: {}", tagId, tag.getTagName());
  }

  private Tag findTagById(Long tagId) {
    return tagRepository
        .findById(tagId)
        .orElseThrow(
            () -> {
              log.error("태그를 찾을 수 없음 - ID: {}", tagId);
              return new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "태그를 찾을 수 없습니다.");
            });
  }

  private void validateTagNameNotExists(String tagName) {
    if (tagRepository.existsByTagName(tagName)) {
      log.error("중복된 태그명 - 태그명: {}", tagName);
      throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 태그명입니다.");
    }
  }
}
