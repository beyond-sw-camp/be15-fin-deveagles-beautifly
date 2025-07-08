package com.deveagles.be15_deveagles_be.features.users.command.application.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileCommandServiceImpl implements FileCommandService {

  private final AmazonS3 amazonS3;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Override
  public String saveProfile(MultipartFile profile) {
    String fileName = "user/thumbnail_" + UUID.randomUUID() + "_" + profile.getOriginalFilename();

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(profile.getSize());
    metadata.setContentType(profile.getContentType());

    try {
      amazonS3.putObject(bucket, fileName, profile.getInputStream(), metadata);
    } catch (IOException e) {
      throw new BusinessException(ErrorCode.FILE_SAVE_ERROR);
    }

    return amazonS3.getUrl(bucket, fileName).toString();
  }
}
