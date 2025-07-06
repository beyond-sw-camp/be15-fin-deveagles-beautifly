package com.deveagles.be15_deveagles_be.features.users.command.application.service;

import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.CreateStaffRequest;
import org.springframework.web.multipart.MultipartFile;

public interface StaffCommandService {
  void staffCreate(Long shopId, CreateStaffRequest staffRequest, MultipartFile profile);
}
