package com.deveagles.be15_deveagles_be.features.users.command.application.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileCommandService {
  String saveProfile(MultipartFile profile);
}
